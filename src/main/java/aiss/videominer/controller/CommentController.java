package aiss.videominer.controller;

import aiss.videominer.exception.ChannelNotFoundException;
import aiss.videominer.exception.CommentForbiddenException;
import aiss.videominer.exception.CommentNotFoundException;
import aiss.videominer.exception.VideoNotFoundException;
import aiss.videominer.model.Caption;
import aiss.videominer.model.Channel;
import aiss.videominer.model.Comment;
import aiss.videominer.model.Video;
import aiss.videominer.repository.ChannelRepository;
import aiss.videominer.repository.CommentRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("videominer/comments")
@Tag(name = "Comentarios", description = "Endpoints para gestionar los comentarios de los videos")
public class CommentController {

    @Autowired
    CommentRepository repository;
    //Get All
    @GetMapping
    @Operation(
            summary = "Obtener todos los comentarios",
            description = "Obtiene una lista paginada de todos los comentarios disponibles",
            tags = { "comentario", "get" }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "comentarios encontrados", content = { @Content(schema = @Schema(implementation = Comment.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "404", description = "No se encontraron comentarios", content = { @Content(schema = @Schema()) })
    })
    public List<Comment> findAll(@Parameter(description = "Número de página")@RequestParam(defaultValue = "0") int page,
                                 @Parameter(description = "Tamaño de la página") @RequestParam(defaultValue = "10") int  size,
                                 @Parameter(description = "Nombre del comentario")@RequestParam(required = false) String name,
                                 @Parameter(description = "Orden de los comentarios")@RequestParam(required = false) String order,
                                 @Parameter(description = "Palabra clave que debe contener el nombre del comentario")@RequestParam(required = false) String containing) throws CommentNotFoundException {
        Pageable paging;
        Page<Comment> pageComment;
        paging = PageRequest.of(page, size);
        pageComment = repository.findAll(paging);
        return pageComment.getContent();
    }
    //Get One
    @GetMapping("/{id}")
    @Operation(
            summary = "Obtener un comentario por su ID",
            description = "Obtiene un comentario especificando su ID.",
            tags = { "comentario", "get" }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "comentario encontrado", content = { @Content(schema = @Schema(implementation = Comment.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "404", description="comentario no encontrado", content = { @Content(schema = @Schema()) })
    })
    public Comment findOne(@Parameter(description = "ID del comentario que se desea obtener")@PathVariable String id) throws CommentNotFoundException, CommentForbiddenException {
        Optional<Comment> comment = repository.findById(id);
        if(!comment.isPresent()) {
            throw new CommentNotFoundException();
        } else if (comment.isEmpty()) {
            throw new CommentForbiddenException();

        }
        return comment.get();
    }
    //CREATE
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @Operation(
            summary = "Crear un nuevo comentario",
            description = "Crea un nuevo comentario utilizando la información proporcionada en el cuerpo de la solicitud",
            tags = { "comentario", "post" }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "comentario creado exitosamente", content = { @Content(schema = @Schema(implementation = Comment.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "400", description="Solicitud inválida", content = { @Content(schema = @Schema()) })
    })
    public Comment create(@Valid @RequestBody Comment comment) {
        return repository.save(comment);
    }

    //UPDATE
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{id}")
    @Operation(
            summary = "Actualizar un comentario",
            description = "Actualiza un comentario existente especificando su ID y proporcionando los datos actualizados en el cuerpo de la solicitud",
            tags = { "comentario", "put" }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "comentario actualizado correctamente", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "404", description = "comentario no encontrado", content = { @Content(schema = @Schema()) })
    })
    public void update(@Valid @RequestBody Comment updatedComment, @Parameter(description = "ID del comentario a actualizar")@PathVariable String id) throws CommentNotFoundException {
        if (!repository.existsById(id)) {
            throw new CommentNotFoundException();
        }
        Comment updated = new Comment(id, updatedComment.getText(), updatedComment.getCreatedOn(), updatedComment.getAuthor());
        repository.save(updated);
    }
    //DELETE
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar un comentario",
            description = "Elimina un comentario existente especificando su ID",
            tags = { "comentario", "delete" }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "comentario eliminado correctamente", content = { @Content(schema = @Schema) }),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta", content = { @Content(schema = @Schema) }),
            @ApiResponse(responseCode = "404", description = "comentario no encontrado", content = { @Content(schema = @Schema) })
    })
    public void delete(@Parameter(description = "ID del comentario a eliminar")@PathVariable String id) throws CommentNotFoundException {
        if (!repository.existsById(id)) {
            throw new CommentNotFoundException();
        }
        repository.deleteById(id);
    }
}
