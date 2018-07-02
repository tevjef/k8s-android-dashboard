package me.tevinjeffrey.kubernetes.home.workloads

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.lifecycle.LiveDataReactiveStreams
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.ChangeBounds
import androidx.transition.Fade
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import androidx.transition.TransitionSet.ORDERING_TOGETHER
import com.google.android.material.chip.Chip
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.ViewHolder
import io.fabric8.kubernetes.api.model.Job
import io.fabric8.kubernetes.api.model.extensions.DaemonSet
import io.fabric8.kubernetes.api.model.extensions.Deployment
import io.fabric8.kubernetes.api.model.extensions.StatefulSet
import io.reactivex.Observable
import io.reactivex.functions.Function4
import kotlinx.android.synthetic.main.fragment_workloads.appBar
import kotlinx.android.synthetic.main.fragment_workloads.emptyState
import kotlinx.android.synthetic.main.fragment_workloads.filterList
import kotlinx.android.synthetic.main.fragment_workloads.list
import kotlinx.android.synthetic.main.fragment_workloads.swipeRefreshLayout
import kotlinx.android.synthetic.main.fragment_workloads.toolbar
import me.tevinjeffrey.kubernetes.base.extensions.getThemePrimaryColor
import me.tevinjeffrey.kubernetes.base.extensions.observe
import me.tevinjeffrey.kubernetes.base.support.BaseFragment
import me.tevinjeffrey.kubernetes.base.support.BaseFragment.SnackbarType.NEGATIVE
import me.tevinjeffrey.kubernetes.entities.kubernetes.extensions.isOK
import me.tevinjeffrey.kubernetes.home.R
import me.tevinjeffrey.kubernetes.home.removeAll
import me.tevinjeffrey.kubernetes.home.settings.adapter.HeaderItem
import me.tevinjeffrey.kubernetes.home.workloads.adapter.CombinedFilter
import me.tevinjeffrey.kubernetes.home.workloads.adapter.NamespaceFilter
import me.tevinjeffrey.kubernetes.home.workloads.adapter.WorkloadItem
import me.tevinjeffrey.kubernetes.home.workloads.adapter.WorkloadTypeFilter
import timber.log.Timber

class WorkloadsFragment : BaseFragment() {

  private val workloadsViewModel by lazy<WorkloadsViewModel> { viewModel() }

  private val emptyConfiguration by lazy { EmptyConfiguration(emptyState as ViewGroup) }

  private val adapter = GroupAdapter<ViewHolder>()
  private val filterAdapter = GroupAdapter<ViewHolder>()
  private val filterClick: MutableLiveData<Boolean> = MutableLiveData()

  private lateinit var searchView: SearchView

  override fun layoutId() = R.layout.fragment_workloads

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)

    list.adapter = adapter
    swipeRefreshLayout.setOnRefreshListener {
      workloadsViewModel.onRefresh()
    }
    toolbar.inflateMenu(R.menu.search_filter)
    toolbar.setOnMenuItemClickListener {
      when (it.itemId) {
        R.id.action_filter -> {
          toolbar.post {
            filterClick.postValue(!(filterClick.value ?: true))
          }
          true
        }
        R.id.action_search -> {

          true
        }
        else -> false
      }
    }
    toolbar.title = getString(R.string.workloads)
    val searchMenuItem = toolbar.menu.findItem(R.id.action_search)
    searchView = searchMenuItem.actionView as SearchView
    val v = searchView.findViewById<View>(androidx.appcompat.R.id.search_plate)
    v.setBackgroundColor(requireContext().getThemePrimaryColor())
    searchView.setOnQueryTextListener(object : OnQueryTextListener {
      override fun onQueryTextSubmit(query: String?): Boolean {

        Timber.d(query)
        return false
      }

      override fun onQueryTextChange(newText: String?): Boolean {
        workloadsViewModel.updateSearchQuery(newText.orEmpty())
        return false
      }

    })

    observe(workloadsViewModel.isLoading) {
      swipeRefreshLayout.isRefreshing = it == true
    }

    observe(workloadsViewModel.error) {
      showSnackbar(it!!.message.orEmpty(), NEGATIVE)
    }

    val deploymentSection = Section()
    deploymentSection.setHideWhenEmpty(true)
    deploymentSection.setHeader(HeaderItem(getString(R.string.deployments)))
    adapter.add(deploymentSection)

    val statefulSetSection = Section()
    statefulSetSection.setHideWhenEmpty(true)
    adapter.add(statefulSetSection)
    statefulSetSection.setHeader(HeaderItem(getString(R.string.stateful_sets)))

    val jobSection = Section()
    jobSection.setHideWhenEmpty(true)
    jobSection.setHeader(HeaderItem(getString(R.string.jobs)))
    adapter.add(jobSection)

    val daemonSetSection = Section()
    daemonSetSection.setHideWhenEmpty(true)
    daemonSetSection.setHeader(HeaderItem(getString(R.string.daemon_sets)))
    adapter.add(daemonSetSection)

    observe(workloadsViewModel.deployments) {
      it ?: return@observe

      val items = it.map {
        WorkloadItem(
            it.metadata.uid,
            it.metadata.name,
            it.status.readyReplicas,
            it.status.replicas,
            it.metadata.labels.keys.toMutableList() + it.metadata.labels.values.toList(),
            it.isOK
        )
      }

      updateWorkloadList(deploymentSection, items)
    }

    observe(workloadsViewModel.statefulSets) {
      it ?: return@observe

      val items = it.map {
        WorkloadItem(
            it.metadata.uid,
            it.metadata.name,
            it.status.readyReplicas ?: 0,
            it.status.replicas ?: 0,
            it.metadata.labels.keys.toMutableList() + it.metadata.labels.values.toList(),
            it.isOK
        )
      }

      updateWorkloadList(statefulSetSection, items)
    }

    observe(workloadsViewModel.daemonSets) {
      it ?: return@observe

      val items = it.map {
        WorkloadItem(
            it.metadata.uid,
            it.metadata.name,
            it.status.currentNumberScheduled ?: 0,
            it.status.desiredNumberScheduled ?: 0,
            it.metadata.labels.keys.toMutableList() + it.metadata.labels.values.toList(),
            it.isOK
        )
      }

      updateWorkloadList(daemonSetSection, items)
    }

    observe(workloadsViewModel.cronJobs) {
      it ?: return@observe

      val items = it.map {
        WorkloadItem(
            it.metadata.uid,
            it.metadata.name,
            it.status.active ?: 0,
            it.status.succeeded ?: 0,
            it.metadata.labels.keys.toMutableList() + it.metadata.labels.values.toList(),
            it.isOK
        )
      }
      updateWorkloadList(jobSection, items)
    }

    Observable.combineLatest(
        Observable.fromPublisher(LiveDataReactiveStreams.toPublisher(viewLifecycleOwner, workloadsViewModel.deployments)),
        Observable.fromPublisher(LiveDataReactiveStreams.toPublisher(viewLifecycleOwner, workloadsViewModel.daemonSets)),
        Observable.fromPublisher(LiveDataReactiveStreams.toPublisher(viewLifecycleOwner, workloadsViewModel.statefulSets)),
        Observable.fromPublisher(LiveDataReactiveStreams.toPublisher(viewLifecycleOwner, workloadsViewModel.cronJobs)),
        Function4 { t1: List<Deployment>, t2: List<DaemonSet>, t3: List<StatefulSet>, t4: List<Job> ->
          emptyConfiguration.isVisible = t1.isEmpty() && t2.isEmpty() && t3.isEmpty() && t4.isEmpty()
        }).subscribe()

    setupFilters()
  }

  private fun updateWorkloadList(section: Section, items: List<WorkloadItem>) {
    val searchItems = items
        .mapNotNull { item ->
          if (searchView.query.toString().isEmpty()) {
            return@mapNotNull item
          }

          val labelsContainQuery = item.labels.any { it.contains(searchView.query, ignoreCase = true) }
          val titleContainsQuery = item.title.contains(searchView.query, ignoreCase = true)

          when {
            titleContainsQuery -> item.copy(highlightSpanText = searchView.query.toString())
            labelsContainQuery -> item
            else -> null
          }
        }

    section.removeAll()
    section.addAll(searchItems)

    list?.post {
      list?.scrollToPosition(0)
    }
  }

  private fun setupFilters() {
    filterList.adapter = filterAdapter
    val namespaceSection = Section()
    val namespaceHeader = HeaderItem(getString(R.string.namespaces))
    namespaceSection.setHideWhenEmpty(true)
    val workloadTypeSection = Section()
    val workloadTypeHeader = HeaderItem(getString(R.string.workload_type))

    var namespaceFilter: NamespaceFilter? = null
    var workloadFilter: WorkloadTypeFilter? = null

    val combinedSection = Section()
    var combinedFilter = CombinedFilter()

    filterAdapter.add(namespaceSection)
    filterAdapter.add(workloadTypeSection)
    filterAdapter.add(combinedSection)

    observe(filterClick) { shouldCollapse ->
      TransitionManager.beginDelayedTransition(appBar, TransitionSet()
          .addTransition(Fade())
          .addTransition(ChangeBounds().addTarget(appBar))
          .setOrdering(ORDERING_TOGETHER)
      )

      combinedFilter = combinedFilter.copy(collapsed = !combinedFilter.collapsed)
      combinedSection.update(listOf(combinedFilter))

      if (shouldCollapse != false) {
        workloadTypeSection.removeHeader()
        namespaceSection.removeHeader()
      } else {
        workloadTypeSection.setHeader(workloadTypeHeader)
        namespaceSection.setHeader(namespaceHeader)
      }

      workloadFilter = workloadFilter?.copy(
          collapsed = shouldCollapse ?: true
      )

      if (workloadFilter != null) {
        workloadTypeSection.update(listOf(workloadFilter))
      }

      namespaceFilter = namespaceFilter?.copy(
          collapsed = shouldCollapse ?: true
      )

      if (namespaceFilter != null) {
        namespaceSection.update(listOf(namespaceFilter))
      }
    }

    observe(workloadsViewModel.namespaceData) {
      it ?: return@observe
      namespaceFilter = it
      namespaceSection.add(it)
      combinedFilter.updateNamespaceChips(it.selectedChips)
      observe(it.mutatedChipsData, true) {
        if (it != null) {
          combinedFilter.updateNamespaceChips(it)
          workloadsViewModel.updateSelectedNamespaces(it)
        }
      }

      combinedFilter = combinedFilter.copy(collapsed = !combinedFilter.collapsed)
      combinedSection.update(listOf(combinedFilter))

      workloadsViewModel.resetNamespaceData()
    }

    observe(workloadsViewModel.workloadTypeData) {
      it ?: return@observe
      workloadFilter = it
      workloadTypeSection.add(it)
      combinedFilter.updateWorkloadTypeChips(it.selectedChips)
      observe(it.mutatedChipsData) {
        if (it != null) {
          combinedFilter.updateWorkloadTypeChips(it)
          workloadsViewModel.updateSelectedWorkloads(it)
        }
      }

      combinedFilter = combinedFilter.copy(collapsed = !combinedFilter.collapsed)
      combinedSection.update(listOf(combinedFilter))

      workloadsViewModel.resetWorkloadTypeData()
    }
  }
}
