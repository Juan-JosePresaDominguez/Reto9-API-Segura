package es.netmind.mypersonalbankapi.controllersAPI;

import es.netmind.mypersonalbankapi.controladores.PrestamosController;
import es.netmind.mypersonalbankapi.exceptions.PrestamoNotFoundException;
import es.netmind.mypersonalbankapi.modelos.prestamos.Prestamo;
import es.netmind.mypersonalbankapi.persistencia.IPrestamosRepoData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Min;
import java.util.List;

/**************************************************************************************************/
/* DOCUMENTACIÓN SWAGGER: http://localhost:9980/swagger-ui/index.html                             */
/* ~~~~~~~~~~~~~~~~~~~~~                                                                          */
/* HISTORIA DE USUARIO 7                                                                          */
/* Como usuario del sistema, quiero poder ver la lista de préstamos de un cliente para entender   */
/* su estado de deuda.                                                                            */
/*                                                                                                */
/* HISTORIA DE USUARIO 8                                                                          */
/* Como usuario del sistema, quiero poder ver el detalle de un préstamo de un cliente para        */
/* conocer su estado y evolución.                                                                 */
/*                                                                                                */
/* HISTORIA DE USUARIO 9                                                                          */
/* Como usuario del sistema, quiero poder evaluar una solicitud de préstamo de cliente, para      */
/* decidir si lo concedemos o no.                                                                 */
/**************************************************************************************************/
@RestController
@RequestMapping("/prestamos")
@Validated
@Tag(name = "MyPersonalBank API", description = "MyPersonalBankAPI - PrestamosControllerAPI")
public class PrestamosControllerAPI {
    private static final Logger logger = LoggerFactory.getLogger(PrestamosControllerAPI.class);

    @Autowired
    private IPrestamosRepoData prestamosRepo;

    @Autowired
    private PrestamosController prestamosController;

    // Método GET (Obtener Préstamos 'getPrestamos')
    @Operation(summary = "Get all loans", description = "Returns all loans from the application")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Not found - The loan was not found")
    })
    @GetMapping(value = "", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<Prestamo>>getPrestamos() throws PrestamoNotFoundException {
        List<Prestamo> prestamos = prestamosRepo.findAll();
        if (prestamos != null && prestamos.size() > 0)
            return new ResponseEntity<>(prestamosRepo.findAll(), HttpStatus.OK); // HTTP 200
            //else return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        else
            throw new PrestamoNotFoundException("La lista de préstamos está vacía: " + prestamos); // HTTP 404 + Excepción: La lista de préstamos está vacía: []
    }

    // Método GET (Obtener Préstamo por ID 'getPrestamo')
    @Operation(summary = "Get a loan by id", description = "Returns a loan as per the id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Not Found - The client was not found")
    })
    @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
//    public ResponseEntity<Prestamo> getPrestamo(
//            @Parameter(name = "id", description = "Prestamo id", example = "1", required = true)
//            @PathVariable @Min(1) Integer id
//    ) {
//        try {
//            return ResponseEntity.status(HttpStatus.OK).body(prestamosController.mostrarDetalle(id)); // HTTP 200
//        } catch (ClienteNotFoundException e) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // HTTP 404
//        }
//        /* La excepción de cliente no encontrado NO se controla aquí, sino en el método mostrarDetalleCliente() de ClientesController. Es la forma más correcta de implementarlo para evitar el anidamiento de if-else. */
//    }
    /* MÉTODO EQUIVALENTE (if-else), controlando la excepción en la propia clase 'PrestamosControllerAPI.java' */
    public ResponseEntity<Prestamo> getCliente(
            @Parameter(name = "id", description = "Prestamo id", example = "1", required = true)
            @PathVariable @Min(1) Integer id
    ) throws Exception {
        Prestamo prestamo = prestamosRepo.findPrestamoById(id);
        if (prestamo != null && prestamo.validar())
            return new ResponseEntity<>(prestamosRepo.findPrestamoById(id), HttpStatus.OK); // HTTP 200
        //else return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        else throw new PrestamoNotFoundException(id); // HTTP 404 + Excepción: 'Cliente con id: X no encontrado'
    }
}
