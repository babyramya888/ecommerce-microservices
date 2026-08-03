using dotnet_notification_service.Models;
using dotnet_notification_service.Services;
using Microsoft.OpenApi.Models;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen(c =>
{
    c.SwaggerDoc("v1", new OpenApiInfo
    {
        Title = "Notification Service API",
        Version = "v1",
        Description = "Notification service for sending order confirmation emails and processing Azure Event Grid events. In-memory storage.",
        Contact = new OpenApiContact { Name = "Ecommerce Team" }
    });
});
builder.Services.AddSingleton<NotificationService>();

var app = builder.Build();

app.UseSwagger();
app.UseSwaggerUI();

app.MapGet("/health", () => Results.Ok(new { status = "Healthy", service = "dotnet-notification-service" }))
    .WithName("health")
    .Produces(200);
app.MapGet("/api/notifications", (NotificationService service) => Results.Ok(service.GetAll()))
    .WithName("GetAllNotifications")
    .Produces(200);
app.MapPost("/api/notifications/send", (NotificationRequest request, NotificationService service) =>
{
    try
    {
        var result = service.Send(request);
        return Results.Ok(result);
    }
    catch (ArgumentException ex)
    {
        return Results.BadRequest(new { message = ex.Message });
    }
})
    .WithName("SendNotification")
    .Produces(200)
    .Produces(400);
app.MapPost("/api/notifications/eventgrid", (OrderPlacedEvent orderEvent, NotificationService service) =>
{
    var result = service.ProcessOrderPlaced(orderEvent);
    return Results.Ok(result);
})
    .WithName("ProcessEventGrid")
    .Produces(200);

app.Run();
