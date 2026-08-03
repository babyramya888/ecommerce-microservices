using dotnet_payment_service.Models;
using dotnet_payment_service.Services;
using Microsoft.OpenApi.Models;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen(c =>
{
    c.SwaggerDoc("v1", new OpenApiInfo
    {
        Title = "Payment Service API",
        Version = "v1",
        Description = "Payment processing service for charging and refunding customer orders. In-memory storage, not yet integrated into the order checkout flow.",
        Contact = new OpenApiContact { Name = "Ecommerce Team" }
    });
});
builder.Services.AddSingleton<PaymentService>();

var app = builder.Build();

app.UseSwagger();
app.UseSwaggerUI();

app.MapGet("/health", () => Results.Ok(new { status = "Healthy", service = "dotnet-payment-service" }))
    .WithName("health")
    .Produces(200);
app.MapGet("/api/payment", (PaymentService service) => Results.Ok(service.GetAll()))
    .WithName("GetAllPayments")
    .Produces(200);
app.MapPost("/api/payment/charge", (PaymentRequest request, PaymentService service) =>
{
    try
    {
        var result = service.Charge(request);
        return Results.Ok(result);
    }
    catch (ArgumentException ex)
    {
        return Results.BadRequest(new { message = ex.Message });
    }
})
    .WithName("Charge")
    .Produces(200)
    .Produces(400);
app.MapPost("/api/payment/refund", (RefundRequest request, PaymentService service) =>
{
    var result = service.Refund(request);
    return Results.Ok(result);
})
    .WithName("Refund")
    .Produces(200);

app.Run();
