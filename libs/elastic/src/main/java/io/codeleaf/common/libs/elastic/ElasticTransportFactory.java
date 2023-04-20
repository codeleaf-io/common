package io.codeleaf.common.libs.elastic;

import co.elastic.clients.json.jsonb.JsonbJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.TransportUtils;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;

import javax.net.ssl.SSLContext;
import java.util.Objects;

public class ElasticTransportFactory {

    private final ElasticClientSettings settings;

    public ElasticTransportFactory(ElasticClientSettings settings) {
        this.settings = settings;
    }

    public static ElasticsearchTransport createTransport(ElasticClientSettings settings) {
        Objects.requireNonNull(settings);
        return new ElasticTransportFactory(settings).createTransport();
    }

    public ElasticsearchTransport createTransport() {
        try {
            SSLContext sslContext = TransportUtils.sslContextFromCaFingerprint(settings.getFingerprint());
            BasicCredentialsProvider credsProv = new BasicCredentialsProvider();
            credsProv.setCredentials(
                    AuthScope.ANY, new UsernamePasswordCredentials(settings.getUser(), settings.getPassword())
            );
            RestClient restClient = RestClient.builder(new HttpHost(settings.getHost(), settings.getPort(), settings.getScheme()))
                    .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder
                            .setSSLContext(sslContext)
                            .setDefaultCredentialsProvider(credsProv))
                    .build();
            return new RestClientTransport(restClient, new JsonbJsonpMapper());
        } catch (RuntimeException cause) {
            throw new RuntimeException("Failed to create transport: " + cause, cause);
        }
    }
}
