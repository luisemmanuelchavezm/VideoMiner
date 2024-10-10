package aiss.videominer.controller;

import aiss.videominer.exception.ChannelNotFoundException;
import aiss.videominer.exception.VideoNotFoundException;
import aiss.videominer.model.Caption;
import aiss.videominer.model.Channel;
import aiss.videominer.model.Comment;
import aiss.videominer.model.Video;
import aiss.videominer.repository.CommentRepository;
import aiss.videominer.repository.VideoRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("videominer/videos")
@Tag(name = "Videos", description = "Endpoints para gestionar los videos")
public class VideoController {

    @Autowired
    VideoRepository repository;

    @GetMapping
    @Operation(
            summary = "Obtener todos los videos",
            description = "Obtiene una lista paginada de todos los videos disponibles",
            tags = { "video", "get" }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "videos encontrados", content = { @Content(schema = @Schema(implementation = Video.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "404", description = "No se encontraron videos", content = { @Content(schema = @Schema()) })
    })
    public List<Video> findAll(@Parameter(description = "Número de página")@RequestParam(defaultValue = "0") int page,
                               @Parameter(description = "Tamaño de la página")@RequestParam(defaultValue = "10") int  size,
                               @Parameter(description = "Nombre del video")@RequestParam(required = false) String name,
                               @Parameter(description = "Orden de los videos")@RequestParam(required = false) String order,
                               @Parameter(description = "Palabra clave que debe contener el nombre del video")@RequestParam(required = false) String containing) throws VideoNotFoundException {
        Pageable paging;
        Page<Video> pageVideo;
        Sort sort;
        if (order != null){
            if (order.startsWith("-")){
                sort =  Sort.by(Sort.Direction.DESC, order.substring(1));
                paging = PageRequest.of(page, size, sort);}
            else{
                sort =  Sort.by(Sort.Direction.ASC, order);
                paging = PageRequest.of(page, size, sort);}
        }
        else
            paging = PageRequest.of(page, size);
        if( name != null)
            pageVideo = repository.findByName(name, paging);
        else{
            if( containing != null)
                pageVideo = repository.findByNameContaining(containing, paging);
            else
                pageVideo = repository.findAll(paging);}
        if (pageVideo.getContent().isEmpty()) throw new VideoNotFoundException();
        return pageVideo.getContent();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Obtener un video por su ID",
            description = "Obtiene un video especificando su ID",
            tags = { "video", "get" }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "video encontrado", content = { @Content(schema = @Schema(implementation = Video.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "404", description="videos no encontrado", content = { @Content(schema = @Schema()) })
    })
    public Video findOne(@Parameter(description = "ID del video que se desea obtener")@PathVariable String id) throws VideoNotFoundException {
        Optional<Video> video = repository.findById(id);
        if(!video.isPresent()) {
            throw new VideoNotFoundException();
        }
        return video.get();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @Operation(
            summary = "Crear un nuevo video",
            description = "Crea un nuevo video utilizando la información proporcionada en el cuerpo de la solicitud",
            tags = { "video", "post" }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "video creado exitosamente", content = { @Content(schema = @Schema(implementation = Video.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "400", description="Solicitud inválida", content = { @Content(schema = @Schema()) })
    })
    public Video create(@Valid @RequestBody Video video) {
        return repository.save(video);
    }



    @GetMapping("/{id}/comments")
    @Operation(
            summary = "Obtener todos los comentarios del video segun su id",
            description = "Obtiene una lista paginada de todos los comentarios disponibles del video",
            tags = { "video", "comentario", "get" }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "comentarios encontrados", content = { @Content(schema = @Schema(implementation = Comment.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "404", description = "No se encontraron comentarios", content = { @Content(schema = @Schema()) })
    })
    public List<Comment> findCommentsVideo(@Parameter(description = "ID del video del que se desea obtener todos sus comentarios")@PathVariable String id) {
        Optional<Video> video = repository.findById(id);
        return video.get().getComments();
    }

    @GetMapping("/{id}/captions")
    @Operation(
            summary = "Obtener todos los captions del video segun su id",
            description = "Obtiene una lista paginada de todos los captions disponibles del video",
            tags = { "video", "caption", "get" }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "captions encontrados", content = { @Content(schema = @Schema(implementation = Caption.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "404", description = "No se encontraron captions", content = { @Content(schema = @Schema()) })
    })
    public List<Caption> findCaptionsVideo(@Parameter(description = "ID del video del que se desea obtener todos sus captions")@PathVariable String id) {
        Optional<Video> video = repository.findById(id);
        return video.get().getCaptions();
    }
    //UPDATE
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{id}")
    @Operation(
            summary = "Actualizar un video",
            description = "Actualiza un video existente especificando su ID y proporcionando los datos actualizados en el cuerpo de la solicitud",
            tags = { "video", "put" }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "video actualizado correctamente", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "404", description = "video no encontrado", content = { @Content(schema = @Schema()) })
    })
    public void update(@Valid @RequestBody Video updatedVideo, @Parameter(description = "ID del video a actualizar")@PathVariable String id) throws VideoNotFoundException {
        if (!repository.existsById(id)) {
            throw new VideoNotFoundException();
        }
        Video newVideo = new Video(id, updatedVideo.getName(), updatedVideo.getDescription(), updatedVideo.getReleaseTime(), updatedVideo.getComments(), updatedVideo.getCaptions());
        repository.save(newVideo);
    }
    //DELETE
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar un video",
            description = "Elimina un video existente especificando su ID",
            tags = { "video", "delete" }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "video eliminado correctamente", content = { @Content(schema = @Schema) }),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta", content = { @Content(schema = @Schema) }),
            @ApiResponse(responseCode = "404", description = "video no encontrado", content = { @Content(schema = @Schema) })
    })
    public void delete(@Parameter(description = "ID del video a eliminar")@PathVariable String id) throws VideoNotFoundException {
        if (!repository.existsById(id)) {
            throw new VideoNotFoundException();
        }
        repository.deleteById(id);
}
}
