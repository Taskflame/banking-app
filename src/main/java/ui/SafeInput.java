package ui;
import models.AccountType;
import java.util.Locale;
import java.util.Scanner;

public final class SafeInput {
    private static final Scanner SC = new Scanner(System.in);
    private SafeInput() {}

    public static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = SC.nextLine().trim();
            try {
                return Integer.parseInt(s);
            }
            catch (NumberFormatException e) {
                System.out.println("Введите целое число. Пример: 123");
            }
        }
    }

    public static double readPositiveMoney(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = SC.nextLine().trim().replace(',','.'); // поддержка ввода с запятой. Заменяем запятую на точку перед парсингом.
            try {
                double v = Double.parseDouble(s);
                if (v <= 0) {
                    System.out.println("Сумма должна быть > 0.");
                    continue;
                }
                return v;
            } catch (NumberFormatException e) {
                System.out.println("Введите число. Пример: 2500.50");
            }
        }
    }

    public static String readNonEmpty(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = SC.nextLine().trim();
            if (!s.isEmpty()) return s;
            System.out.println("Строка не может быть пустой.");
        }
    }

    public static AccountType readAccountType(String prompt) {
        while (true) {
            System.out.print(prompt + " [personal/business/vip]: ");
            String s = SC.nextLine().trim().toLowerCase(Locale.ROOT); // нормализуем ввод к нижнему регистру в независимой от системы локали
            switch (s) {
                case "personal": return AccountType.PERSONAL;
                case "business": return AccountType.BUSINESS;
                case "vip": return AccountType.VIP;
                default:
                    System.out.println("Неизвестный тип. Допустимо: personal, business, vip.");
            }
        }
    }
}
