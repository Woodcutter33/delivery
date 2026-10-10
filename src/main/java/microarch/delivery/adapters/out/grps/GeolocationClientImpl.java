package microarch.delivery.adapters.out.grps;

import clients.geo.GeoGrpc;
import clients.geo.GeoProto;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import jakarta.annotation.PreDestroy;
import libs.errs.Error;
import libs.errs.Result;
import microarch.delivery.ApplicationProperties;
import microarch.delivery.core.domain.model.Address;
import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.ports.GeolocationClient;
import org.springframework.stereotype.Service;

@Service
public class GeolocationClientImpl implements GeolocationClient {

    private final ManagedChannel channel;
    private final GeoGrpc.GeoBlockingStub stub;

    public GeolocationClientImpl(ApplicationProperties properties) {
        this.channel = ManagedChannelBuilder.forAddress(properties.getGrpc().getGeoService().getHost(),
                properties.getGrpc().getGeoService().getPort()).usePlaintext().build();
        this.stub = GeoGrpc.newBlockingStub(channel);
    }

    @PreDestroy
    public void shutdown() {
        if (!channel.isShutdown()) {
            channel.shutdown();
        }
    }

    @Override
    public Location getLocation(Address address) {
        GeoProto.GetGeolocationRequest request = GeoProto.GetGeolocationRequest.newBuilder()
                .setStreet(address.getStreet()).build();
        GeoProto.GetGeolocationReply reply = stub.getGeolocation(request);

        if (!reply.hasLocation()) {
            throw new IllegalStateException("GEO service response does not contain a location");
        }

        GeoProto.Location coordinates = reply.getLocation();
        Result<Location, Error> locationResult = Location.create(coordinates.getX(), coordinates.getY());
        if (locationResult.isFailure()) {
            throw new IllegalStateException(
                    "GEO service returned invalid coordinates: " + locationResult.getError().getMessage());
        }

        return locationResult.getValue();
    }
}
