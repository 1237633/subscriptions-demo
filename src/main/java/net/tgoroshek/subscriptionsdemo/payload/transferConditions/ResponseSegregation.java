package net.tgoroshek.subscriptionsdemo.payload.transferConditions;

public interface ResponseSegregation {

    interface ShortDetails {
    }

    interface Details extends ShortDetails {
    }

    interface FullDetails extends Details {
    }

    /**
     * Интерфейс для отметки тех полей, которые никогда не должны быть показаны пользователю.
     * Этот интерфейс никогда нельзя использовать для выбора группы валидации возвращаемого обьекта.
     * Только для отметки полей.
     */
    interface NeverShown {

    }
}
