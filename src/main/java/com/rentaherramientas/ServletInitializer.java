package com.rentaherramientas;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Punto de entrada cuando la aplicacion se despliega como WAR en un Tomcat
 * externo.
 *
 * Con empaquetado JAR esta clase no hace nada: el arranque lo sigue manejando
 * el metodo main() de RentaHerramientasApplication. Se deja preparada para que
 * cambiar a WAR sea solamente modificar una linea del pom.xml, sin tocar codigo.
 *
 * El motivo de que exista: cuando Tomcat despliega un WAR no ejecuta ningun
 * main(), sino que busca una clase que extienda SpringBootServletInitializer
 * para saber como iniciar el contexto de Spring.
 *
 * @author Daniel Osorio & Camilo García
 */
public class ServletInitializer extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(RentaHerramientasApplication.class);
    }
}
