namespace dotnet_payment_service.Models;

public class PaymentRequest
{
    public string OrderId { get; set; } = string.Empty;
    public string CustomerEmail { get; set; } = string.Empty;
    public decimal Amount { get; set; }
    public string Currency { get; set; } = "USD";
}

public class RefundRequest
{
    public string OrderId { get; set; } = string.Empty;
    public string Reason { get; set; } = string.Empty;
}

public class PaymentResult
{
    public string Id { get; set; } = string.Empty;
    public string OrderId { get; set; } = string.Empty;
    public string Status { get; set; } = "succeeded";
    public decimal Amount { get; set; }
    public string Currency { get; set; } = "USD";
    public string Message { get; set; } = string.Empty;
}

public class PaymentRecord : PaymentResult
{
    public DateTimeOffset CreatedAt { get; set; }
    public DateTimeOffset? RefundedAt { get; set; }
}
