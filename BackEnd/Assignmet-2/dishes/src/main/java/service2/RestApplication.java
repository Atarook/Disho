package service2;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

@ApplicationPath("/api") // Base URI for all REST endpoints
public class RestApplication extends Application {
    // No need to override anything if you're using annotations like @Path in your REST resources
}
