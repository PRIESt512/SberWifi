package ru.sbrf.sberwifi.http.report

import okhttp3.OkHttpClient
import ru.sbrf.sberwifi.MainContext
import javax.net.ssl.X509TrustManager

class UnsafeOkHttpClient {

    companion object {

        public fun getUnsafeOkHttpClient(): OkHttpClient {
            /*val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                @Throws(CertificateException::class)
                override fun checkClientTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {
                }

                @Throws(CertificateException::class)
                override fun checkServerTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {
                }

                override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> {
                    return arrayOf(X509Certificate)
                }
            })*/

            val sslSocketFactory = MainContext.INSTANCE.sslContext.socketFactory;
            val builder = OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, MainContext.INSTANCE.trustManager[0] as X509TrustManager);
            builder.hostnameVerifier { hostname, session ->
                return@hostnameVerifier true
            }
            val okHttpClient = builder.build()
            return okHttpClient
        }
    }
}