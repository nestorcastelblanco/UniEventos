package co.edu.uniquindio.UniEventos.controller;

import co.edu.uniquindio.UniEventos.dto.CuentaDTOs.CrearCuentaDTO;
import co.edu.uniquindio.UniEventos.dto.CuentaDTOs.EditarCuentaDTO;
import co.edu.uniquindio.UniEventos.dto.CuentaDTOs.InformacionCuentaDTO;
import co.edu.uniquindio.UniEventos.servicios.interfaces.CuentaServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/cuenta");
public class CuentaController {

    private final CuentaServicio cuentaServicio;

    @PostMapping("/crear-cuenta")
    public void crearCuenta(CrearCuentaDTO crearCuentaDTO) throws Exception{
    }

    public void editarCuenta(EditarCuentaDTO editarCuentaDTO) throws Exception{

    }

    public void eliminarCuenta(String id) throws Exception{

    }

    public InformacionCuentaDTO obtenerInformacionCuenta(String id) throws Exception{
        return null;
    }


}
