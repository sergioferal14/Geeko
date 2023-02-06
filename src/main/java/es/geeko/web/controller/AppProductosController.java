package es.geeko.web.controller;

import es.geeko.dto.ProductoDto;
import es.geeko.dto.UsuarioDto;
import es.geeko.model.Producto;
import es.geeko.repository.ProductoRepository;
import es.geeko.service.ProductoService;
import es.geeko.service.UsuarioService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Optional;

@Controller
public class AppProductosController extends AbstractController<ProductoDto> {

    private final ProductoService productoService;
    private final ProductoRepository productoRepository;
    private final UsuarioService usuarioService;


    public AppProductosController(ProductoService service, ProductoRepository productoRepository, UsuarioService usuarioService) {
        this.productoService = service;
        this.productoRepository = productoRepository;
        this.usuarioService = usuarioService;
    }


    @GetMapping("/productos/libros")
    public String vistaLibro(ModelMap interfazConPantalla){
        final List<Producto> listaProductos = productoRepository.findProductosByLibroAndActivo(1,1);
        interfazConPantalla.addAttribute("listaProductos",listaProductos);
        final List<Producto> listaNovedades = productoRepository.findProductosByLibroAndUsuarioIsNull(1);
        interfazConPantalla.addAttribute("listaNovedades",listaNovedades);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<UsuarioDto> usuarioDto = this.usuarioService.encuentraPorId(this.usuarioService.getRepo().findUsuarioByEmilio(username).get().getId());

        UsuarioDto attr = usuarioDto.get();
        interfazConPantalla.addAttribute("datosUsuario",attr);
        return "/productos/libros";
    }

    @GetMapping("/productos/peliculas")
    public String vistaPelis(ModelMap interfazConPantalla){
        final List<Producto> listaProductos = productoRepository.findProductosByPeliculaAndActivo(1,1);
        interfazConPantalla.addAttribute("listaProductos",listaProductos);
        final List<Producto> listaNovedades = productoRepository.findProductosByPeliculaAndUsuarioIsNull(1);
        interfazConPantalla.addAttribute("listaNovedades",listaNovedades);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<UsuarioDto> usuarioDto = this.usuarioService.encuentraPorId(this.usuarioService.getRepo().findUsuarioByEmilio(username).get().getId());

        UsuarioDto attr = usuarioDto.get();
        interfazConPantalla.addAttribute("datosUsuario",attr);
        return "/productos/peliculas";
    }

    @GetMapping("/productos/series")
    public String vistaSerie(ModelMap interfazConPantalla){
        final List<Producto> listaProductos = productoRepository.findProductosBySerieAndActivo(1,1);
        interfazConPantalla.addAttribute("listaProductos",listaProductos);
        final List<Producto> listaNovedades = productoRepository.findProductosBySerieAndUsuarioIsNull(1);
        interfazConPantalla.addAttribute("listaNovedades",listaNovedades);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<UsuarioDto> usuarioDto = this.usuarioService.encuentraPorId(this.usuarioService.getRepo().findUsuarioByEmilio(username).get().getId());

        UsuarioDto attr = usuarioDto.get();
        interfazConPantalla.addAttribute("datosUsuario",attr);
        return "/productos/series";
    }

    @GetMapping("/productos/videojuegos")
    public String vistaVideojuegos(ModelMap interfazConPantalla){
        final List<Producto> listaProductos = productoRepository.findProductosByVideojuegoAndActivo(1,1);
        interfazConPantalla.addAttribute("listaProductos",listaProductos);
        final List<Producto> listaNovedades = productoRepository.findProductosByVideojuegoAndUsuarioIsNull(1);
        interfazConPantalla.addAttribute("listaNovedades",listaNovedades);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<UsuarioDto> usuarioDto = this.usuarioService.encuentraPorId(this.usuarioService.getRepo().findUsuarioByEmilio(username).get().getId());

        UsuarioDto attr = usuarioDto.get();
        interfazConPantalla.addAttribute("datosUsuario",attr);
        return "/productos/videojuegos";
    }


    @GetMapping("/productos/crearproducto")
    public String vistaCrearProducto(ModelMap interfazConPantalla){
        //Instancia en memoria del dto a informar en la pantalla
        final ProductoDto productoDto = new ProductoDto();
        //Mediante "addAttribute" comparto con la pantalla
        interfazConPantalla.addAttribute("datosProducto",productoDto);
        return "productos/crearproducto";
    }

    @PostMapping("/productos/crearproducto")
    public String guardarProducto(ProductoDto productoDto) throws Exception {
        //LLamo al método del servicio para guardar los datos
        ProductoDto productoGuardado =  this.productoService.guardar(productoDto);
        System.out.println("Titulo = " + productoGuardado.getTitulo());
        System.out.println("Imagen = " + productoGuardado.getImagen());
        System.out.println("Descripcion = " + productoGuardado.getDescripcion());
        Long id = productoGuardado.getId();
        return "productos/productopropio";
    }

    /*@GetMapping("productos/productopropio")
    public String vistaProducto(ProductoDto productoDto, Integer id, ModelMap interfazConPantalla) throws Exception{

        Optional<ProductoDto> producto = productoService.encuentraPorId(id);

        if (producto.isPresent()){

            //LLamo al método del servicio para guardar los datos
            ProductoDto productoDtoGuardar =  new ProductoDto();
            productoDtoGuardar.setId(Long.valueOf(id));
            productoDtoGuardar.setTitulo(productoDto.getTitulo());
            productoDtoGuardar.setDescripcion(productoDto.getDescripcion());
            productoDtoGuardar.setImagen(productoDto.getImagen());

            this.productoService.guardar(productoDtoGuardar);
            interfazConPantalla.addAttribute("datosProducto",productoDtoGuardar);
            return "/productos/productopropio";
        } else {
            //Mostrar página usuario no existe
            return "error";
        }


    }

*/

    @PostMapping("/productos/{idusr}")
    public String guardarEdicionDatosUsuario(@PathVariable("idusr") Integer id, ProductoDto productoEntrada) throws Exception {
        //Cuidado que la password no viene
        //Necesitamos copiar la información que llega menos la password
        //Con el id tengo que buscar el registro a nivel de entidad
        Optional<ProductoDto> productoDtoControl = this.productoService.encuentraPorId(id);
        //¿Debería comprobar si hay datos?
        if (productoDtoControl.isPresent()){

            //LLamo al método del servicio para guardar los datos
            ProductoDto productoDtoGuardar =  new ProductoDto();
            productoDtoGuardar.setId(Long.valueOf(id));
            productoDtoGuardar.setTitulo(productoEntrada.getTitulo());
            productoDtoGuardar.setDescripcion(productoEntrada.getDescripcion());

            this.productoService.guardar(productoDtoGuardar);
            return String.format("redirect:/productos/{idusr}", id);
        } else {
            //Mostrar página usuario no existe
            return "error";
        }
    }


}
