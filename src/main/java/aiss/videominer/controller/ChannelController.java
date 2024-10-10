package aiss.videominer.controller;

import aiss.videominer.exception.ChannelNotFoundException;
import aiss.videominer.model.Channel;
import aiss.videominer.repository.ChannelRepository;
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
@RequestMapping("videominer/channels")
@Tag(name = "Canales", description = "Endpoints para gestionar los canales")
public class ChannelController {

    @Autowired
    ChannelRepository repository;

    @GetMapping
    @Operation(
            summary = "Obtener todos los canales",
            description = "Obtiene una lista paginada de todos los canales disponibles",
            tags = { "canales", "get" }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Canales encontrados", content = { @Content(schema = @Schema(implementation = Channel.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "404", description = "No se encontraron canales", content = { @Content(schema = @Schema()) })
    })
    public List<Channel> findAll(@Parameter(description = "Número de página")@RequestParam(defaultValue = "0") int page,
                                 @Parameter(description = "Tamaño de la página")@RequestParam(defaultValue = "10") int  size,
                                 @Parameter(description = "Nombre del canal")@RequestParam(required = false) String name,
                                 @Parameter(description = "Orden de clasificación") @RequestParam(required = false) String order,
                                 @Parameter(description = "Palabra clave que debe contener el nombre del canal")@RequestParam(required = false) String containing) throws ChannelNotFoundException {
        Pageable paging;
        Page<Channel> pageChannel;
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
            pageChannel = repository.findByName(name, paging);
        else{
            if( containing != null)
                pageChannel = repository.findByNameContaining(containing, paging);
            else
                pageChannel = repository.findAll(paging);}
        if (pageChannel.getContent().isEmpty()) throw new ChannelNotFoundException();
        return pageChannel.getContent();
    }
    //Get
    @GetMapping("/{id}")
    @Operation(
            summary = "Obtener un canal por su ID",
            description = "Obtiene un canal especificando su ID",
            tags = { "canales", "get" }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Canal encontrado", content = { @Content(schema = @Schema(implementation = Channel.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "404", description="Canal no encontrado", content = { @Content(schema = @Schema()) })
    })
    public Channel findOne(@Parameter(description = "ID del canal que se desea obtener")@PathVariable String id) throws ChannelNotFoundException {
        Optional<Channel> channel = repository.findById(id);
        if (!channel.isPresent()) {
            throw new ChannelNotFoundException();
        }
        return channel.get();
    }
    //CREATE
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @Operation(
            summary = "Crear un nuevo canal",
            description = "Crea un nuevo canal utilizando la información proporcionada en el cuerpo de la solicitud",
            tags = { "canal", "post" }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Canal creado exitosamente", content = { @Content(schema = @Schema(implementation = Channel.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "400", description="Solicitud inválida", content = { @Content(schema = @Schema()) })
    })
    public Channel create(@Valid @RequestBody Channel channel) {
        return repository.save(channel);
    }

    //Update
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{id}")
    @Operation(
            summary = "Actualizar un canal",
            description = "Actualiza un canal existente especificando su ID y proporcionando los datos actualizados en el cuerpo de la solicitud",
            tags = { "canal", "put" }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "canal actualizado correctamente", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "404", description = "canal no encontrado", content = { @Content(schema = @Schema()) })
    })
    public void update(@Valid @RequestBody Channel updatedChannel, @Parameter(description = "ID del canal a actualizar")@PathVariable String id) throws ChannelNotFoundException {
        if (!repository.existsById(id)) {
            throw new ChannelNotFoundException();
        }

        Channel putChannel = new Channel(id, updatedChannel.getName(), updatedChannel.getVideos(), updatedChannel.getDescription(), updatedChannel.getCreatedTime());
        repository.save(putChannel);
    }
    //DELETE
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar un canal",
            description = "Elimina un canal existente especificando su ID",
            tags = { "canal", "delete" }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "canal eliminado correctamente", content = { @Content(schema = @Schema) }),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta", content = { @Content(schema = @Schema) }),
            @ApiResponse(responseCode = "404", description = "canal no encontrado", content = { @Content(schema = @Schema) })
    })
    public void delete(@Parameter(description = "ID del canal a eliminar")@PathVariable String id) throws ChannelNotFoundException {
        if (!repository.existsById(id)) {
            throw new ChannelNotFoundException(); // Throwing the exception if the channel does not exist
        }
        repository.deleteById(id);
    }
}
