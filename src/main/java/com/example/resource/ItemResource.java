package com.example.resource;

import com.example.model.Item;
import com.example.service.ItemService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;

import java.net.URI;
import java.util.List;

/**
 * REST endpoint for managing Items.
 * Demonstrates a complete CRUD API with OpenAPI documentation.
 */
@Path("/api/items")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Items", description = "Item management endpoints")
public class ItemResource {

    private static final Logger LOG = Logger.getLogger(ItemResource.class);

    @Inject
    ItemService itemService;

    @GET
    @Operation(summary = "Get all items", description = "Returns a list of all items")
    @APIResponse(
            responseCode = "200",
            description = "List of items",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(type = SchemaType.ARRAY, implementation = Item.class)
            )
    )
    public List<Item> getAll() {
        LOG.info("Getting all items");
        return itemService.findAll();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get item by ID", description = "Returns a single item by its ID")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Item found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = Item.class)
                    )
            ),
            @APIResponse(responseCode = "404", description = "Item not found")
    })
    public Response getById(
            @Parameter(description = "Item ID", required = true)
            @PathParam("id") String id) {
        LOG.infof("Getting item with id: %s", id);
        return itemService.findById(id)
                .map(item -> Response.ok(item).build())
                .orElse(Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("Item not found", "ID: " + id))
                        .build());
    }

    @POST
    @Operation(summary = "Create a new item", description = "Creates a new item and returns it")
    @APIResponses({
            @APIResponse(
                    responseCode = "201",
                    description = "Item created",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = Item.class)
                    )
            ),
            @APIResponse(responseCode = "400", description = "Invalid input")
    })
    public Response create(Item item) {
        LOG.infof("Creating new item: %s", item.getName());
        
        if (item.getName() == null || item.getName().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Validation error", "Name is required"))
                    .build();
        }
        
        Item created = itemService.create(item);
        return Response.created(URI.create("/api/items/" + created.getId()))
                .entity(created)
                .build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Update an item", description = "Updates an existing item")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Item updated",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = Item.class)
                    )
            ),
            @APIResponse(responseCode = "404", description = "Item not found")
    })
    public Response update(
            @Parameter(description = "Item ID", required = true)
            @PathParam("id") String id,
            Item item) {
        LOG.infof("Updating item with id: %s", id);
        return itemService.update(id, item)
                .map(updated -> Response.ok(updated).build())
                .orElse(Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("Item not found", "ID: " + id))
                        .build());
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete an item", description = "Deletes an item by its ID")
    @APIResponses({
            @APIResponse(responseCode = "204", description = "Item deleted"),
            @APIResponse(responseCode = "404", description = "Item not found")
    })
    public Response delete(
            @Parameter(description = "Item ID", required = true)
            @PathParam("id") String id) {
        LOG.infof("Deleting item with id: %s", id);
        if (itemService.delete(id)) {
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND)
                .entity(new ErrorResponse("Item not found", "ID: " + id))
                .build();
    }

    @GET
    @Path("/count")
    @Operation(summary = "Get item count", description = "Returns the total number of items")
    @APIResponse(
            responseCode = "200",
            description = "Item count",
            content = @Content(mediaType = MediaType.APPLICATION_JSON)
    )
    public Response count() {
        return Response.ok(new CountResponse(itemService.count())).build();
    }

    // Response DTOs
    public record ErrorResponse(String error, String message) {}
    public record CountResponse(long count) {}
}
