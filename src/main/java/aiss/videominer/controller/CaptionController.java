package aiss.videominer.controller;

import aiss.videominer.exception.CaptionNotFoundException;
import aiss.videominer.model.Caption;
import aiss.videominer.model.Channel;
import aiss.videominer.repository.CaptionRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
@RequestMapping("videominer/captions")
@Tag(name = "Captions", description = "Endpoints para gestionar los captions de los videos")
public class CaptionController {
    private final CaptionRepository repository;

    public CaptionController(CaptionRepository repository) {
        this.repository = repository;
    }

    // Get All
    @GetMapping
    @Operation(
            summary = "Obtener todos los captions",
            description = "Obtener una lista paginada de todos los captions disponibles",
            tags = { "caption", "get" }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "captions encontrados", content = { @Content(schema = @Schema(implementation = Caption.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "404", description="captions no encontrados", content = { @Content(schema = @Schema()) })
    })
    public List<Caption> findAll(@Parameter(description = "Número de página, comenzando desde 0")@RequestParam(defaultValue = "0") int page,
                                 @Parameter(description = "Tamaño de la página")@RequestParam(defaultValue = "10") int  size,
                                 @Parameter(description = "Nombre del caption")@RequestParam(required = false) String name,
                                 @Parameter(description = "Orden de los captions")@RequestParam(required = false) String order,
                                 @Parameter(description = "Palabra clave que deben contener los captions", required = false)@RequestParam(required = false) String containing) {
        Pageable paging;
        Page<Caption> pageComment;
        paging = PageRequest.of(page, size);
        pageComment = repository.findAll(paging);
        return pageComment.getContent();
    }
    //GET One
    @GetMapping("/{id}")
    @Operation(
            summary = "Obtener un único caption",
            description = "Obtiene un único caption según su ID",
            tags = { "caption", "get" }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "caption encontrado", content = { @Content(schema = @Schema(implementation = Caption.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "404", description = "caption no encontrado", content = { @Content(schema = @Schema()) })
    })
    public Caption findOne(@Parameter(description = "ID del caption a obtener")@PathVariable String id) throws CaptionNotFoundException {
        Optional<Caption> caption = repository.findById(id);
        if(!caption.isPresent()) {
            throw new CaptionNotFoundException();
        }
        return caption.get();
    }
    //CREATE
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @Operation(
            summary = "Crear un nuevo caption",
            description = "Crea un nuevo caption utilizando la información proporcionada en el cuerpo de la solicitud",
            tags = { "caption", "post" }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "caption creado exitosamente", content = { @Content(schema = @Schema(implementation = Caption.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "400", description="Solicitud inválida", content = { @Content(schema = @Schema()) })
    })
    public Caption create(@Valid @RequestBody Caption caption) {
        return repository.save(caption);
    }
    //UPDATE
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{id}")
    @Operation(
            summary = "Actualizar un caption",
            description = "Actualiza un caption existente según su ID",
            tags = { "caption", "put" }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "caption actualizado correctamente", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "404", description = "caption no encontrado", content = { @Content(schema = @Schema()) })
    })
    public void update(@Valid @RequestBody Caption updatedCaption, @Parameter(description = "ID del caption a actualizar")@PathVariable String id) throws CaptionNotFoundException {
        if (!repository.existsById(id)) {
            throw new CaptionNotFoundException();
        }

        Caption putCaption = new Caption(id, updatedCaption.getName(), updatedCaption.getLanguage());
        repository.save(putCaption);
    }

    //DELETE
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar un caption",
            description = "Elimina un caption existente especificando su ID",
            tags = { "caption", "delete" }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "caption eliminado correctamente", content = { @Content(schema = @Schema) }),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta", content = { @Content(schema = @Schema) }),
            @ApiResponse(responseCode = "404", description = "caption no encontrado", content = { @Content(schema = @Schema) })
    })
    public void delete(@Parameter(description = "ID del caption a eliminar")@PathVariable String id) throws CaptionNotFoundException {
        boolean exists = repository.existsById(id);
        if (!exists) {
            throw new CaptionNotFoundException();
        }
        repository.deleteById(id);
    }
}
