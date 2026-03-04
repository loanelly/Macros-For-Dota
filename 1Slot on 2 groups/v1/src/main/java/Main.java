import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main implements NativeKeyListener {
    private Robot robot;
    private Random random = new Random();
    private volatile boolean isProcessing = false;

    public Main() {
        try {
            robot = new Robot();
            // Отключаем лишние логи в консоли
            Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
            logger.setLevel(Level.OFF);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            // Регистрация глобального хука
            GlobalScreen.registerNativeHook();
            GlobalScreen.addNativeKeyListener(new Main());
            System.out.println("--- Макрос запущен! Нажми ПРОБЕЛ для теста ---");
        } catch (NativeHookException ex) {
            System.err.println("Ошибка регистрации: " + ex.getMessage());
        }
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        // Код клавиши Пробел в JNativeHook
        if (e.getKeyCode() == NativeKeyEvent.VC_SPACE && !isProcessing) {
            isProcessing = true;

            new Thread(() -> {
                try {
                    // Случайная пауза перед нажатием 6 и второго пробела
                    int delay = 100 + random.nextInt(201); // 100-300 мс
                    Thread.sleep(delay);

                    // Нажимаем клавишу 6
                    robot.keyPress(KeyEvent.VK_6);
                    robot.keyRelease(KeyEvent.VK_6);

                    // Нажимаем клавишу Space
                    robot.keyPress(KeyEvent.VK_SPACE);
                    robot.keyRelease(KeyEvent.VK_SPACE);

                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                } finally {
                    // Разрешаем макросу сработать снова
                    isProcessing = false;
                }
            }).start();
        }
    }

    @Override public void nativeKeyReleased(NativeKeyEvent e) {}
    @Override public void nativeKeyTyped(NativeKeyEvent e) {}
}
