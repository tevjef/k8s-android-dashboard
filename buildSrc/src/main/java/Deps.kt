import Versions.archNavigationVersion
import Versions.archPagingVersion
import Versions.archRoomVersion
import Versions.archVersion
import Versions.archWorkVersion
import Versions.chuckVersion
import Versions.daggerVersion
import Versions.debugdrawerVersion
import Versions.glideVersion
import Versions.gsonVersion
import Versions.instabugVersion
import Versions.junitVersion
import Versions.kotlinVersion
import Versions.leakCanaryVersion
import Versions.okhttpVersion
import Versions.retrofitVersion
import Versions.rxandroidVersion
import Versions.rxbindingVersion
import Versions.rxjavaVersion
import Versions.storeVersion
import Versions.supportLibraryVersion

@Suppress("MayBeConstant", "MemberVisibilityCanBePrivate", "unused")
object Deps {

  // Support Libraries
  val appcompatV7 = "com.android.support:appcompat-v7:$supportLibraryVersion"
  val design = "com.android.support:design:$supportLibraryVersion"
  val recyclerviewV7 = "com.android.support:recyclerview-v7:$supportLibraryVersion"
  val supportAnnotations = "com.android.support:support-annotations:$supportLibraryVersion"
  val supportV4 = "com.android.support:support-v4:$supportLibraryVersion"
  val gridLayout = "com.android.support:gridlayout-v7:$supportLibraryVersion"
  val cardView = "com.android.support:cardview-v7:$supportLibraryVersion"
  val customtabs = "com.android.support:customtabs:$supportLibraryVersion"
  val constraintLayout = "com.android.support.constraint:constraint-layout:1.1.0-beta5"
  val multidex = "com.android.support:multidex:1.0.3"
  val materialDesignComponents = "com.google.android.material:material:1.0.0-alpha1"

  // Play Services
  // https://developers.google.com/android/guides/setup
  val playAuth = "com.google.android.gms:play-services-auth:15.0.0"

  // Firebase
  // https://firebase.google.com/support/release-notes/android
  val firebaseConfig = "com.google.firebase:firebase-config:15.0.2"
  val firebaseCore = "com.google.firebase:firebase-core:15.0.2"
  val firebasePerf = "com.google.firebase:firebase-perf:15.2.0"
  val firebaseMessaging = "com.google.firebase:firebase-messaging:15.0.2"

  val autoDispose = "com.uber.autodispose:autodispose-android-archcomponents-kotlin:0.8.0"
  val autoDisposeTest = "com.uber.autodispose:autodispose-android-archcomponents-test:0.8.0"

  /**
   * Architecture Components
   */

  // ViewModel and LiveData
  val archEx = "android.arch.lifecycle:extensions:$archVersion"
  val archJava = "android.arch.lifecycle:common-java8:$archVersion"
  val archRuntime = "android.arch.lifecycle:runtime:$archVersion"
  val archViewModel = "android.arch.lifecycle:viewmodel:$archVersion"
  val archReactiveStreams = "android.arch.lifecycle:reactivestreams:$archVersion"
  // optional - Test helpers for LiveData
  val archCoreTesting = "android.arch.core:core-testing:$archVersion"

  // Navigation
  val archNavigationFragment = "android.arch.navigation:navigation-fragment-ktx:$archNavigationVersion"
  val archNavigation = "android.arch.navigation:navigation-ui-ktx:$archNavigationVersion"

  // Room
  val archRoomRuntime = "android.arch.persistence.room:runtime:$archRoomVersion"
  val archRoomProcessor = "android.arch.persistence.room:compiler:$archRoomVersion"
  val archRoomRxJava = "android.arch.persistence.room:rxjava2:$archRoomVersion"
  val archRoomTesting = "android.arch.persistence.room:testing:$archRoomVersion"

  // Paging
  val archPagingRuntime = "android.arch.paging:runtime:$archPagingVersion"
  // optional - RxJava support, currently in alpha
  val archPagingRxJava = "android.arch.paging:rxjava2:1.0.0-alpha1"
  // alternatively - without Android dependencies for testing
  val archPagingTesting =  "android.arch.paging:common:$archPagingVersion"

  // Worker
  val archWorkRuntime = "android.arch.work:work-runtime-ktx:$archWorkVersion"
  // optional - Firebase JobDispatcher support
  val archWorkFirebase = "android.arch.work:work-firebase:$archWorkVersion"
  // optional - Test helpers
  val archWorkTesting = "android.arch.work:work-testing:$archWorkVersion"

  val androidKtx = "androidx.core:core-ktx:1.0.0-alpha3"

  val arch = listOf(
      archEx,
      archJava,
      archReactiveStreams,
      archNavigationFragment,
      archNavigation,
      archRoomRuntime,
      archRoomRxJava,
      archPagingRuntime,
      archPagingRxJava
// https://issuetracker.google.com/u/1/issues/109962764
//      archWorkRuntime,
//      archWorkFirebase
  ) + listOf(
      autoDispose,
      androidKtx
  )

  val archAP = listOf(
      archRoomProcessor
  )

  val archTesting = listOf(
      archCoreTesting,
      archRoomTesting,
      archPagingTesting,
      archWorkTesting,
      autoDisposeTest
  )

  // Api
  val retrofit = "com.squareup.retrofit2:retrofit:$retrofitVersion"
  val converterGson = "com.squareup.retrofit2:converter-gson:$retrofitVersion"
  val adapterRxjava = "com.squareup.retrofit2:adapter-rxjava2:$retrofitVersion"
  val okhttp = "com.squareup.okhttp3:okhttp:$okhttpVersion"
  val loggingInterceptor = "com.squareup.okhttp3:logging-interceptor:$okhttpVersion"
  val okio = "com.squareup.okio:okio:1.14.1"
  val gson = "com.google.code.gson:gson:$gsonVersion"

  // Store
  val storeKotlin = "com.nytimes.android:store-kotlin3:$storeVersion"
  val storeCache = "com.nytimes.android:cache3:$storeVersion"
  val storeGson = "com.nytimes.android:middleware3:$storeVersion"
  val storeFileSystem = "com.nytimes.android:filesystem3:$storeVersion"

  // Kotlin
  val kotlinStdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlinVersion"

  // Dagger
  // https://github.com/google/dagger/releases
  const val annotationsApi = "javax.annotation:javax.annotation-api:1.2"
  val dagger = "com.google.dagger:dagger:$daggerVersion"
  val daggerCompiler = "com.google.dagger:dagger-compiler:$daggerVersion"
  val daggerAndroidSupport = "com.google.dagger:dagger-android-support:$daggerVersion"
  val daggerAndroidCompiler = "com.google.dagger:dagger-android-processor:$daggerVersion"


  // Glide
  val glide = "com.github.bumptech.glide:glide:$glideVersion"
  val glideCompiler = "com.github.bumptech.glide:compiler:$glideVersion"

  // Rx Bindings
  const val rxbindingGroupId = "com.jakewharton.rxbinding2"
  val rxbinding = "$rxbindingGroupId:rxbinding-kotlin:$rxbindingVersion"
  val rxbindingSupportV4 = "$rxbindingGroupId:rxbinding-support-v4-kotlin:$rxbindingVersion"
  val rxbindingAppcompatV7 = "$rxbindingGroupId:rxbinding-appcompat-v7-kotlin:$rxbindingVersion"
  val rxbindingDesign = "$rxbindingGroupId:rxbinding-design-kotlin:$rxbindingVersion"
  val rxbindingRecyclerview = "$rxbindingGroupId:rxbinding-recyclerview-v7:$rxbindingVersion"

  // Debug Drawer
  // https://github.com/palaima/DebugDrawer
  val debugdrawer = "io.palaima.debugdrawer:debugdrawer-view:$debugdrawerVersion"
  val debugdrawerNoOp = "io.palaima.debugdrawer:debugdrawer-view-no-op:$debugdrawerVersion"
  val processPhoenix = "com.jakewharton:process-phoenix:2.0.0"

  // Debug Drawer Ext
  val debugdrawerCommons = "io.palaima.debugdrawer:debugdrawer-commons:$debugdrawerVersion"
  val debugdrawerActions = "io.palaima.debugdrawer:debugdrawer-actions:$debugdrawerVersion"
  val debugdrawerOkhttp3 = "io.palaima.debugdrawer:debugdrawer-okhttp3:$debugdrawerVersion"
  val debugdrawerPicasso = "io.palaima.debugdrawer:debugdrawer-picasso:$debugdrawerVersion"
  val debugdrawerGlide = "io.palaima.debugdrawer:debugdrawer-glide:$debugdrawerVersion"
  val debugdrawerTimber = "io.palaima.debugdrawer:debugdrawer-timber:$debugdrawerVersion"
  val debugdrawerLogs = "io.palaima.debugdrawer:debugdrawer-logs:$debugdrawerVersion"
  val debugdrawerNetworkQuality = "io.palaima.debugdrawer:debugdrawer-network-quality:$debugdrawerVersion"

  val debugDrawerExt = listOf(
      debugdrawerCommons,
      debugdrawerActions,
      debugdrawerOkhttp3,
      debugdrawerPicasso,
      debugdrawerGlide,
      debugdrawerLogs,
      debugdrawerNetworkQuality,
      debugdrawerTimber
  )

  // Libraries
  val bottomNavigation = "com.aurelhubert:ahbottomnavigation:2.1.0"
  val crashlytics = "com.crashlytics.sdk.android:crashlytics:2.9.2@aar"
  val conceal = "com.facebook.conceal:conceal:2.0.2@aar"
  val timber = "com.jakewharton.timber:timber:4.6.1"
  val chuck = "com.readystatesoftware.chuck:library:$chuckVersion"
  val chuckNoOp = "com.readystatesoftware.chuck:library-no-op:$chuckVersion"
  val facebookSdk = "com.facebook.android:facebook-android-sdk:4.30.0"
  val hockey = "net.hockeyapp.android:HockeySDK:5.1.0"
  val inject = "javax.inject:javax.inject:1"
  val instabug = "com.instabug.library:instabug:$instabugVersion"
  val instabugInterceptor = "com.instabug.library:instabug-with-okhttp-interceptor:$instabugVersion"
  val leakCanary = "com.squareup.leakcanary:leakcanary-android:$leakCanaryVersion"
  val leakCanaryNoOp = "com.squareup.leakcanary:leakcanary-android-no-op:$leakCanaryVersion"
  val lottie = "com.airbnb.android:lottie:2.5.0"
  val materialDialogs = "com.afollestad.material-dialogs:core:0.9.6.0"
  val pageIndicator = "com.github.chahinem:pageindicator:0.2.5"
  val patrons = "com.github.prolificinteractive:patrons:introduce_change_listeners-SNAPSHOT"
  val picasso = "com.squareup.picasso:picasso:2.6.0-SNAPSHOT"
  val rxjava = "io.reactivex.rxjava2:rxjava:$rxjavaVersion"
  val rxkotlin = "io.reactivex.rxjava2:rxkotlin:2.2.0"
  val rxandroid = "io.reactivex.rxjava2:rxandroid:$rxandroidVersion"
  val koptional = "com.gojuno.koptional:koptional-rxjava2-extensions:1.5.0"
  val shimmer = "io.supercharge:shimmerlayout:1.1.0"
  val threetenabp = "com.jakewharton.threetenabp:threetenabp:1.1.0"
  val groupie = "com.xwray:groupie:2.0.3"
  val groupieKotlin = "com.xwray:groupie-kotlin-android-extensions:2.0.3"
  val fbSonar = "com.facebook.sonar:sonar:0.0.1"

  // Testing
  val jUnit = "junit:junit:$junitVersion"
  val mockito = "org.mockito:mockito-core:2.18.3"
  val hamcrest = "org.hamcrest:hamcrest-all:1.3"
  val kluent = "org.amshove.kluent:kluent:1.38"
  val kluentAndroid = "org.amshove.kluent:kluent-android:1.38"
  val testRunner = "com.android.support.test:runner:1.0.2"
  val testRules = "com.android.support.test:rules:1.0.2"
  val espressoCore = "com.android.support.test.espresso:espresso-core:3.0.2"
  val roboelectric = "org.robolectric:robolectric:4.0-alpha-2"

  // Dependency Group
  val rx = listOf(rxjava, rxandroid, rxkotlin, koptional)

  val rxBindings = listOf(
      rxbinding,
      rxbindingSupportV4,
      rxbindingAppcompatV7,
      rxbindingDesign,
      rxbindingRecyclerview
  )

  val api = listOf(
      retrofit,
      converterGson,
      adapterRxjava,
      okhttp,
      loggingInterceptor,
      okio,
      gson
  )

  val store = listOf(
      storeKotlin,
      storeCache,
      storeGson,
      storeFileSystem
  )

  val supportLibs = listOf(
      appcompatV7,
      cardView,
      gridLayout,
      constraintLayout,
      customtabs,
      multidex,
      recyclerviewV7,
      supportAnnotations,
      supportV4,
      materialDesignComponents
  )

  val firebase = listOf(
      firebaseConfig,
      firebaseCore,
      firebasePerf,
      firebaseMessaging)

  val testLibs = listOf(jUnit, mockito, hamcrest, kluent, kluentAndroid, roboelectric)

  val androidTestLibs = listOf(testRules, testRunner, espressoCore)

  val featureProjects = listOf(
      ":auth",
      ":home"
  )
}

