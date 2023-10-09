import java.util.Random;

class Account {
    private int balance;

    public Account(int initialBalance) {
        this.balance = initialBalance;
    }

    public synchronized void deposit(int amount) {
        balance += amount;
        System.out.println("Пополнено: " + amount + " Баланс: " + balance);
        notify(); // Уведомляем ожидающие потоки, что баланс изменился
    }

    public synchronized void withdraw(int amount) {
        while (balance < amount) {
            try {
                System.out.println("Ожидание пополнения для снятия " + amount);
                wait(); // Ожидаем, пока не будет достаточно средств на счете
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        balance -= amount;
        System.out.println("Снято: " + amount + " Баланс: " + balance);
    }
}

public class Main {
    public static void main(String[] args) {
        Account account = new Account(0);

        // Поток для многократного пополнения счета
        Thread depositThread = new Thread(() -> {
            Random random = new Random();
            while (true) {
                int amount = random.nextInt(100); // Генерируем случайную сумму для пополнения
                account.deposit(amount);
                try {
                    Thread.sleep(1000); // Ждем некоторое время перед следующим пополнением
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        // Поток для снятия денег
        Thread withdrawThread = new Thread(() -> {
            while (true) {
                int amountToWithdraw = 50; // Сумма, которую хотим снять
                account.withdraw(amountToWithdraw);
                try {
                    Thread.sleep(2000); // Ждем перед следующей попыткой снятия
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        depositThread.start();
        withdrawThread.start();
    }
}
