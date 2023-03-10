package es.geeko.web.controller;

import es.geeko.dto.ProductoDto;
import es.geeko.dto.UsuarioDto;
import es.geeko.model.Comentario;
import es.geeko.model.Producto;
import es.geeko.model.Tematica;
import es.geeko.repository.ProductoRepository;
import es.geeko.service.ComentarioService;
import es.geeko.service.ProductoService;
import es.geeko.service.TematicaService;
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
    private final TematicaService tematicaService;
    private final ComentarioService comentarioService;


    public AppProductosController(ProductoService service, ProductoRepository productoRepository, UsuarioService usuarioService, TematicaService tematicaService, ComentarioService comentarioService) {
        this.productoService = service;
        this.productoRepository = productoRepository;
        this.usuarioService = usuarioService;
        this.tematicaService = tematicaService;
        this.comentarioService = comentarioService;
    }


    @GetMapping("/productos/libros")
    public String vistaLibro(ModelMap interfazConPantalla){

        //Listas de libros
        final List<Producto> listaProductos = productoRepository.findProductosByLibroAndActivoAndGeekoIsOrderByIdDesc(1,1,0);
        interfazConPantalla.addAttribute("listaProductos",listaProductos);
        final List<Producto> listaNovedades = productoRepository.findProductosByLibroAndActivoAndGeekoIsOrderByIdDesc(1,1,1);
        interfazConPantalla.addAttribute("listaNovedades",listaNovedades);

        //M??todo para mostrar los datos del usuario de la sesi??n
        usuarioSesion(interfazConPantalla);

        return "/productos/libros";

    }

    @GetMapping("/productos/peliculas")
    public String vistaPelis(ModelMap interfazConPantalla){

        //Listas de peliculas
        final List<Producto> listaProductos = productoRepository.findProductosByPeliculaAndActivoAndGeekoIsOrderByIdDesc(1,1,0);
        interfazConPantalla.addAttribute("listaProductos",listaProductos);
        final List<Producto> listaNovedades = productoRepository.findProductosByPeliculaAndActivoAndGeekoIsOrderByIdDesc(1,1,1);
        interfazConPantalla.addAttribute("listaNovedades",listaNovedades);

        usuarioSesion(interfazConPantalla);

        return "/productos/peliculas";
    }

    @GetMapping("/productos/series")
    public String vistaSerie(ModelMap interfazConPantalla){

        //Listas de series
        final List<Producto> listaProductos = productoRepository.findProductosBySerieAndActivoAndGeekoIsOrderByIdDesc(1,1,0);
        interfazConPantalla.addAttribute("listaProductos",listaProductos);
        final List<Producto> listaNovedades = productoRepository.findProductosBySerieAndActivoAndGeekoIsOrderByIdDesc(1,1,1);
        interfazConPantalla.addAttribute("listaNovedades",listaNovedades);

        usuarioSesion(interfazConPantalla);

        return "/productos/series";

    }

    @GetMapping("/productos/videojuegos")
    public String vistaVideojuegos(ModelMap interfazConPantalla){

        //Listas de videojuegos
        final List<Producto> listaProductos = productoRepository.findProductosByVideojuegoAndActivoAndGeekoIsOrderByIdDesc(1,1,0);
        interfazConPantalla.addAttribute("listaProductos",listaProductos);
        final List<Producto> listaNovedades = productoRepository.findProductosByVideojuegoAndActivoAndGeekoIsOrderByIdDesc(1,1,1);
        interfazConPantalla.addAttribute("listaNovedades",listaNovedades);

        usuarioSesion(interfazConPantalla);

        return "/productos/videojuegos";

    }


    @GetMapping("/productos/crearproducto")
    public String vistaCrearProducto(ModelMap interfazConPantalla){

        //Mostrar datos de usuario de la sesi??n y sus intereses
        usuarioSesionConIntereses(interfazConPantalla);

        //Creamos el DTO del producto a crear
        final ProductoDto productoDto = new ProductoDto();

        //Se asigna una imagen por defecto, a cambiar por el usuario
        productoDto.setImagen("/imagenes/noimage.jpg");
        interfazConPantalla.addAttribute("datosProducto",productoDto);

        //Lista de tem??ticas para asignar al producto
        final List<Tematica> tematicas = tematicaService.buscarEntidades();
        interfazConPantalla.addAttribute("listaTematicas",tematicas);

        return "productos/crearproducto";

    }

    @PostMapping("/productos/crearproducto")
    public String guardarProducto(ProductoDto productoDto) throws Exception {

        //Asignamos al usuario de la sesi??n como propietario del producto
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        productoDto.setUsuario(this.usuarioService.getRepo().getUsuarioByIdIs(this.usuarioService.getRepo().findUsuarioByEmilio(username).get().getId()));

        //Condicionales en la creaci??n
        if(productoDto.getActivo() == null){
            productoDto.setActivo(1);
        }

        if(productoDto.getLibro() == null){
            productoDto.setLibro(0);
        }
        if(productoDto.getVideojuego() == null){
            productoDto.setVideojuego(0);
        }
        if(productoDto.getSerie() == null){
            productoDto.setSerie(0);
        }
        if(productoDto.getPelicula() == null){
            productoDto.setPelicula(0);
        }
        if(productoDto.getLibro() == 0 && productoDto.getVideojuego() == 0 && productoDto.getSerie() == 0 && productoDto.getPelicula() == 0){
            productoDto.setLibro(1);
        }
        if(productoDto.getGeeko() == null){
            productoDto.setGeeko(0);
        }

        //Guardamos y redireccionamos
        this.productoService.guardar(productoDto);

        return "redirect:/productos/subidos";
    }

    @GetMapping("/productos/{idpro}")
    public String vistaProducto(@PathVariable("idpro") Integer id, ModelMap interfazConPantalla){

        //Buscamos el producto por ID
        Optional<ProductoDto> productoDto = productoService.encuentraPorId(id);

        //Si est?? presente, mostramos en la pantalla
        if(productoDto.isPresent()) {

            usuarioSesionConIntereses(interfazConPantalla);

            //Lista de comentarios del producto
            final List<Comentario> listaComentarios = this.comentarioService.getRepo().findComentariosByProductoIdAndActivoIsOrderByIdDesc(id, 1);
            interfazConPantalla.addAttribute("listaComentarios", listaComentarios);

            //Datos del producto
            ProductoDto productoDtoMostrar= productoDto.get();
            interfazConPantalla.addAttribute("datosProducto", productoDtoMostrar);

            return "productos/producto";

        } else{
            return "error";
        }
    }

    @GetMapping("/productos/edit/{idpro}")
    public String vistaEditProducto(@PathVariable("idpro") Integer id, ModelMap interfazConPantalla){

        usuarioSesionConIntereses(interfazConPantalla);

        //Buscamos por ID el producto a editar
        Optional<ProductoDto> productoDto = this.productoService.encuentraPorId(id);

        //Si el producto est?? presente, mostramos
        if(productoDto.isPresent()) {

            //Lista de tem??ticas para asignar
            final List<Tematica> tematicas = tematicaService.buscarEntidades();
            interfazConPantalla.addAttribute("listaTematicas", tematicas);

            //Datos del producto a editar
            ProductoDto editar = productoDto.get();
            interfazConPantalla.addAttribute("datosProducto", editar);

            return "productos/crearproducto";

        } else{
            return "error";
        }
    }

    @PostMapping("/productos/edit/{idpro}")
    public String editProducto(@PathVariable("idpro") Integer id, ProductoDto productoDtoEntrada) throws Exception {

        //Necesitamos el DTO del usuario de la sesi??n
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<UsuarioDto> usuarioDto = this.usuarioService.encuentraPorId(this.usuarioService.getRepo().findUsuarioByEmilio(username).get().getId());

        //Si est?? presente, actuamos
        if(usuarioDto.isPresent()) {

            UsuarioDto attr = usuarioDto.get();

            //Generamos un producto DTO para editar y le asignamos los datos del productoDtoEntrada
            ProductoDto productoDtoGuardar = new ProductoDto();
            productoDtoGuardar.setId(id);

            if (productoDtoEntrada.getVideojuego() == null) {
                productoDtoEntrada.setVideojuego(0);
            }
            if (productoDtoEntrada.getLibro() == null) {
                productoDtoEntrada.setLibro(0);
            }
            if (productoDtoEntrada.getSerie() == null) {
                productoDtoEntrada.setSerie(0);
            }
            if (productoDtoEntrada.getPelicula() == null) {
                productoDtoEntrada.setPelicula(0);
            }
            if (productoDtoEntrada.getLibro() == 0 && productoDtoEntrada.getVideojuego() == 0 && productoDtoEntrada.getSerie() == 0 && productoDtoEntrada.getPelicula() == 0) {
                productoDtoEntrada.setLibro(1);
            }
            if (productoDtoEntrada.getGeeko() == null) {
                productoDtoEntrada.setGeeko(0);
            }
            if (productoDtoEntrada.getActivo() == null) {
                productoDtoEntrada.setActivo(1);
            }

            productoDtoGuardar.setTitulo(productoDtoEntrada.getTitulo());
            productoDtoGuardar.setImagen(productoDtoEntrada.getImagen());
            productoDtoGuardar.setDescripcion(productoDtoEntrada.getDescripcion());
            productoDtoGuardar.setPrecio(productoDtoEntrada.getPrecio());

            //Asignamos el usuario de la sesi??n como propietario
            productoDtoGuardar.setUsuario(this.usuarioService.getMapper().toEntity(attr));
            productoDtoGuardar.setPuntuacion(productoDtoEntrada.getPuntuacion());
            productoDtoGuardar.setVideojuego(productoDtoEntrada.getVideojuego());
            productoDtoGuardar.setLibro(productoDtoEntrada.getLibro());
            productoDtoGuardar.setPelicula(productoDtoEntrada.getPelicula());
            productoDtoGuardar.setSerie(productoDtoEntrada.getSerie());
            productoDtoGuardar.setReportado(productoDtoEntrada.getReportado());
            productoDtoGuardar.setActivo(productoDtoEntrada.getActivo());
            productoDtoGuardar.setFechaSubida(productoDtoEntrada.getFechaSubida());
            productoDtoGuardar.setComentario(productoDtoEntrada.getComentario());
            productoDtoGuardar.setTematica(productoDtoEntrada.getTematica());
            productoDtoGuardar.setProductosReportados(productoDtoEntrada.getProductosReportados());
            productoDtoGuardar.setGeeko(productoDtoEntrada.getGeeko());

            //Guardamos
            this.productoService.guardar(productoDtoGuardar);

            return "redirect:/productos/subidos";

        } else{
            return "error";
        }
    }


    @GetMapping("/productos/subidos")
    public String vistaSubidos(ModelMap interfazConPantalla){

        //Obtenemos el usuario actual
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<UsuarioDto> usuarioDto = this.usuarioService.encuentraPorId(this.usuarioService.getRepo().findUsuarioByEmilio(username).get().getId());

        //Si est?? presente, mostramos
        if(usuarioDto.isPresent()) {

            //Mostramos usu datos
            UsuarioDto attr = usuarioDto.get();
            interfazConPantalla.addAttribute("datosUsuario", attr);

            //Mostramos los productos de su propiedad
            final List<Producto> listaProductos = productoRepository.findProductosByUsuarioIdAndActivoIsOrderByIdDesc(attr.getId(), 1);
            interfazConPantalla.addAttribute("listaProductos", listaProductos);

            return "productos/productossubidos";

        } else{
            return "error";
        }
    }

    public void usuarioSesion(ModelMap interfazConPantalla){

        //Obtenemos el DTO del usuario actual por ID
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<UsuarioDto> usuarioDto = this.usuarioService.encuentraPorId(this.usuarioService.getRepo().findUsuarioByEmilio(username).get().getId());

        //Si est?? presente, mostramos sus datos
        if(usuarioDto.isPresent()) {
            UsuarioDto attr = usuarioDto.get();
            interfazConPantalla.addAttribute("datosUsuario", attr);
        }


    }

    public void usuarioSesionConIntereses(ModelMap interfazConPantalla){

        //Obtenemos el DTO del usuario actual por ID
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<UsuarioDto> usuarioDto = this.usuarioService.encuentraPorId(this.usuarioService.getRepo().findUsuarioByEmilio(username).get().getId());

        //Si est?? presente, mostramos sus datos e intereses
        if(usuarioDto.isPresent()) {
            UsuarioDto attr = usuarioDto.get();
            interfazConPantalla.addAttribute("datosUsuario", attr);

            final List<Producto> listaIntereses = productoRepository.findTop5ProductosByTematicaIsInAndGeekoIsAndActivoIsOrderByIdDesc(usuarioDto.get().getTematicas(), 1, 1);
            interfazConPantalla.addAttribute("listaIntereses", listaIntereses);
        }

    }



}
