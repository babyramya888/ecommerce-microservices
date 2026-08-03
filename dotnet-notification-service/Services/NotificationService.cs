using dotnet_notification_service.Models;

namespace dotnet_notification_service.Services;

public class NotificationService
{
    private readonly List<NotificationRecord> _notifications = new();

    public NotificationResult Send(NotificationRequest request)
    {
        if (string.IsNullOrWhiteSpace(request.RecipientEmail))
        {
            throw new ArgumentException("Recipient email is required.");
        }

        if (string.IsNullOrWhiteSpace(request.Message))
        {
            request.Message = BuildMessage(request);
        }

        var notification = new NotificationRecord
        {
            Id = Guid.NewGuid().ToString("N"),
            EventType = request.EventType,
            OrderId = request.OrderId,
            Channel = request.Channel,
            RecipientEmail = request.RecipientEmail,
            Message = request.Message,
            Status = "queued",
            CreatedAt = DateTimeOffset.UtcNow
        };

        _notifications.Add(notification);
        return notification;
    }

    public NotificationResult ProcessOrderPlaced(OrderPlacedEvent orderEvent)
    {
        var request = new NotificationRequest
        {
            EventType = "OrderPlaced",
            OrderId = orderEvent.OrderId,
            RecipientEmail = orderEvent.CustomerEmail,
            RecipientName = orderEvent.CustomerName,
            Message = $"Hello {orderEvent.CustomerName}, your order {orderEvent.OrderId} has been confirmed for {orderEvent.TotalAmount:C}."
        };

        return Send(request);
    }

    public IReadOnlyList<NotificationRecord> GetAll() => _notifications;

    private static string BuildMessage(NotificationRequest request)
    {
        return request.EventType switch
        {
            "OrderPlaced" => $"Order {request.OrderId} has been placed successfully.",
            "PaymentSucceeded" => $"Payment for order {request.OrderId} was completed.",
            _ => $"Notification for order {request.OrderId}"
        };
    }
}

public class OrderPlacedEvent
{
    public string OrderId { get; set; } = string.Empty;
    public string CustomerEmail { get; set; } = string.Empty;
    public string CustomerName { get; set; } = string.Empty;
    public decimal TotalAmount { get; set; }
}
