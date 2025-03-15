package kz.gz.ticktack.util;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.RECT;
import com.sun.jna.platform.win32.WinUser.WNDENUMPROC;
import com.sun.jna.win32.StdCallLibrary;
import kz.gz.ticktack.data.config.Constants;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;


@Slf4j
public class EcpSigner extends Thread {
    public EcpSigner(String ecpPassword, String ecpAbsPath) throws AWTException {
        this.ecpPassword = ecpPassword;
        this.ecpAbsPath = ecpAbsPath;
        this.robot = new Robot();
    }

    private Robot robot;
    //Конфигурация
    private final int CHOOSE_WINDOW_HEIGHT_THRESHOLD = 80; //Макс. высота окна без сертификата
    private static final String CHOOSE_CERTIFICATE = "Выберите сертификат";
    private static final String TYPE_PASSWORD = "Введите пароль:";
    private static final String OPEN_FILE = "Формирование ЭЦП";
    private static final String ERROR = "Ошибка";
    private static final long ACTION_SLEEP_TIME = 5;

    private String ecpPassword;
    private String ecpAbsPath;
    private boolean IS_ECP_CHOOSEN = false;

    public interface User32 extends StdCallLibrary {
        User32 INSTANCE = (User32) Native.loadLibrary("user32", User32.class);

        boolean EnumWindows(WNDENUMPROC lpEnumFunc, Pointer arg);

        int GetWindowTextW(HWND hWnd, char[] lpString, int nMaxCount);

        boolean GetWindowRect(HWND hWnd, RECT rect);

        boolean IsWindowVisible(HWND hWnd);

        HWND GetForegroundWindow();

        boolean SetForegroundWindow(HWND hWnd);
    }

    @Override
    public void run() {
        log.debug("7Z7SaJFF0g :: ECP SIGNER STARTED.");
        while (Constants.ECP_SIGNER_STATUS) {
            try {
                findAndClickWindow();
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        log.debug("7Z7SaJFF0g :: ECP SIGNER STOPPED.");
    }

    private void findAndClickWindow() {
        final User32 user32 = User32.INSTANCE;
        user32.EnumWindows(new WNDENUMPROC() {
            @SneakyThrows
            @Override
            public boolean callback(HWND hWnd, Pointer arg1) {
                char[] windowText = new char[512];
                user32.GetWindowTextW(hWnd, windowText, 512);
                String wText = new String(windowText).trim();
                if (!user32.IsWindowVisible(hWnd)) {
                    return true;
                }
                HWND foregroundWindow = user32.GetForegroundWindow();
                if (wText.equals(OPEN_FILE)) {
                    if (!hWnd.equals(foregroundWindow)) {
                        user32.SetForegroundWindow(hWnd);
                    }
                    log.debug("ECP Window found");
                    chooseCertificate(hWnd);
                    Constants.CERTIFICATE_CLICKED = true;
                    return false;
                }
                if (wText.equals(TYPE_PASSWORD)) {
                    log.debug("Password Window found");
                    try {
                        typePassword(hWnd);
                        Constants.CERTIFICATE_CLICKED = false;
                    } catch (AWTException e) {
                        throw new RuntimeException(e);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    return false;
                }
                if (wText.equals(CHOOSE_CERTIFICATE)) {
                    Thread.sleep(500);
                    if (!hWnd.equals(foregroundWindow)) {
                        user32.SetForegroundWindow(hWnd);
                    }
                    log.debug("OpenFile Window found");
                    try {
                        copyAndPastText(ecpAbsPath);
                        Thread.sleep(1000);
                        Constants.CERTIFICATE_CLICKED = false;
                    } catch (AWTException e) {
                        throw new RuntimeException(e);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    return false;
                }
                if (wText.equals(ERROR)) {
                    if (!hWnd.equals(foregroundWindow)) {
                        user32.SetForegroundWindow(hWnd);
                    }
                    log.debug("ECP Window found");
                    clickXClose(hWnd);
                    return false;
                }
                return true;
            }
        }, null);
    }

    private void chooseCertificate(HWND hWnd) {
        RECT rect = new RECT();
        if (User32.INSTANCE.GetWindowRect(hWnd, rect)) {
            int centerX = (rect.left + rect.right) / 2;
            int centerY = (rect.top + rect.bottom) / 2;
            int height = rect.bottom - rect.top;
            if (height > CHOOSE_WINDOW_HEIGHT_THRESHOLD) {
                clickByCoordinates(centerX, centerY);
            } else {
                clickByCoordinates(centerX, centerY + height / 4);
            }
        }
    }

    private void clickXClose(HWND hWnd) {
        RECT rect = new RECT();
        if (User32.INSTANCE.GetWindowRect(hWnd, rect)) {
            int centerX = rect.right - 25;
            int centerY = rect.top + 25;
            clickByCoordinates(centerX, centerY);
        }
    }

    private void clickByCoordinates(int x, int y) {
        try {
            Thread.sleep(ACTION_SLEEP_TIME);
            robot.mouseMove(x, y);
            Thread.sleep(ACTION_SLEEP_TIME);
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            Thread.sleep(ACTION_SLEEP_TIME);
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            Thread.sleep(ACTION_SLEEP_TIME);
        } catch (Exception e) {
            log.error("XhAaIc9IWSw :: ECP SIGNER ERROR : message : {}", e.getMessage(), e);
        }
    }

    private void typePassword(HWND hWnd) throws AWTException, InterruptedException {
        RECT rect = new RECT();
        if (User32.INSTANCE.GetWindowRect(hWnd, rect)) {
            copyAndPastText(ecpPassword);
            if (!IS_ECP_CHOOSEN) {
                IS_ECP_CHOOSEN = true;
            }
        }
    }

    private void copyAndPastText(String text) throws AWTException, InterruptedException {
        StringSelection stringSelection = new StringSelection(text);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
        Thread.sleep(ACTION_SLEEP_TIME);
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_V);
        Thread.sleep(ACTION_SLEEP_TIME);
        robot.keyRelease(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_CONTROL);
        Thread.sleep(ACTION_SLEEP_TIME);
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);
        Thread.sleep(ACTION_SLEEP_TIME);
    }
}
