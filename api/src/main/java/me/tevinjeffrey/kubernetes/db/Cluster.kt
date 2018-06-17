package me.tevinjeffrey.kubernetes.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Config is a tuple of references to a cluster (how do I communicate with a kubernetes cluster),
 * a user (how do I identify myself), and a namespace (what subset of resources do I want to work with)
 *
 * Adapted from:
 * https://github.com/kubernetes/kubernetes/blob/master/staging/src/k8s.io/client-go/tools/clientcmd/api/v1/types.go
 */
@Entity(tableName = "cluster")
data class Cluster(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "cluster_id")
    val clusterId: Int = 0,

    /**
     * The nickname of this cluster
     */
    @ColumnInfo(name = "name")
    val name: String?,

    /**
     * Server is the address of the kubernetes cluster (https://hostname:port).
     */
    @ColumnInfo(name = "server")
    val server: String? = null,

    /**
     * 	InsecureSkipTLSVerify Skips the validity check for the server's certificate.
     * 	This will make your HTTPS connections insecure.
     */
    @ColumnInfo(name = "insecure_skip_tls_verify")
    val insecureSkipTLSVerify: Boolean = false,

    /**
     * CertificateAuthority contains PEM-encoded certificate authority certificates.
     */
    @ColumnInfo(name = "certificate_authority")
    val certificateAuthority: String? = null,

    /**
     *  Token is the bearer token for authentication to the kubernetes cluster.
     */
    @ColumnInfo(name = "token")
    val token: String? = null,

    /**
     * ClientCertificate contains PEM-encoded data from a client cert file for TLS.
     */
    @ColumnInfo(name = "client_certificate")
    val clientCertificate: String? = null,

    /**
     * ClientKey contains PEM-encoded data from a client key file for TLS.
     */
    @ColumnInfo(name = "client_key")
    val clientKey: String? = null,

    /**
     * Username is the username for basic authentication to the kubernetes cluster.
     */
    @ColumnInfo(name = "username")
    val username: String? = null,

    /**
     * Password is the password for basic authentication to the kubernetes cluster.
     */
    @ColumnInfo(name = "password")
    val password: String? = null,

    /**
     * Should connections to this cluster be proxied.
     */
    @ColumnInfo(name = "should_proxy")
    val shouldProxy: Boolean = false,

    /**
     * The Proxy url
     */
    @ColumnInfo(name = "proxy_url")
    val proxyUrl: String? = null
)