namespace dotnet_notification_service.Models;

public class NotificationRequest
{
    public string EventType { get; set; } = "OrderPlaced";
    public string OrderId { get; set; } = string.Empty;
    public string RecipientEmail { get; set; } = string.Empty;
    public string RecipientName { get; set; } = string.Empty;
    public string Message { get; set; } = string.Empty;
    public string Channel { get; set; } = "email";
}

public class NotificationResult
{
    public string Id { get; set; } = string.Empty;
    public string EventType { get; set; } = string.Empty;
    public string Status { get; set; } = "queued";
    public string RecipientEmail { get; set; } = string.Empty;
    public string Message { get; set; } = string.Empty;
    public DateTimeOffset CreatedAt { get; set; }
}

public class NotificationRecord : NotificationResult
{
    public string OrderId { get; set; } = string.Empty;
    public string Channel { get; set; } = "email";
}
