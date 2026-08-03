using dotnet_payment_service.Models;

namespace dotnet_payment_service.Services;

public class PaymentService
{
    private readonly List<PaymentRecord> _payments = new();

    public PaymentResult Charge(PaymentRequest request)
    {
        if (string.IsNullOrWhiteSpace(request.OrderId))
        {
            throw new ArgumentException("Order id is required.");
        }

        if (request.Amount <= 0)
        {
            return new PaymentResult
            {
                Id = Guid.NewGuid().ToString("N"),
                OrderId = request.OrderId,
                Status = "failed",
                Amount = request.Amount,
                Currency = request.Currency,
                Message = "Payment failed: amount must be greater than zero."
            };
        }

        var payment = new PaymentRecord
        {
            Id = Guid.NewGuid().ToString("N"),
            OrderId = request.OrderId,
            Status = "succeeded",
            Amount = request.Amount,
            Currency = request.Currency,
            Message = "Payment processed successfully.",
            CreatedAt = DateTimeOffset.UtcNow
        };

        _payments.Add(payment);
        return payment;
    }

    public PaymentResult Refund(RefundRequest request)
    {
        var payment = _payments.LastOrDefault(x => x.OrderId == request.OrderId && x.Status == "succeeded");
        if (payment == null)
        {
            return new PaymentResult
            {
                Id = Guid.NewGuid().ToString("N"),
                OrderId = request.OrderId,
                Status = "failed",
                Amount = 0,
                Message = "Refund failed: no successful payment was found for this order."
            };
        }

        payment.Status = "refunded";
        payment.Message = string.IsNullOrWhiteSpace(request.Reason) ? "Refund processed successfully." : $"Refund processed successfully: {request.Reason}";
        payment.RefundedAt = DateTimeOffset.UtcNow;
        return payment;
    }

    public IReadOnlyList<PaymentRecord> GetAll() => _payments;
}
