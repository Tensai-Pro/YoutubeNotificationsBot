import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

public class NotificationsBot extends TelegramLongPollingBot {

    private final String BOT_NAME = "YTNotifsBot";
    private final String BOT_TOKEN = "5469181407:AAHUnthBbAOdoSQRvuyTwcxtoLZ70LMcV0M";

    @Override
    public String getBotUsername() {
        return BOT_NAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message msg = update.getMessage();
        if (msg.hasText()) {
            SendMessage sendMsg = new SendMessage();
            sendMsg.setChatId(msg.getChatId().toString());
            setButtons(sendMsg);

            try {
                switch(msg.getText()) {
                    case "/start":
                        sendMsg.setText("Hi! Wanna see some streams?");
                        execute(sendMsg);
                        break;
                    case "Miko Ch.":
                        sendMsg.setText(StreamData.retrieveInfo("UC-hM6YJuNYVAmUWxeIr9FeA"));
                        execute(sendMsg);
                        break;
                    case "Korone Ch.":
                        sendMsg.setText(StreamData.retrieveInfo("UChAnqc_AY5_I3Px5dig3X1Q"));
                        execute(sendMsg);
                        break;
                    default:
                        sendMsg.setText("Huh?");
                        execute(sendMsg);
                        break;
                }
            }
            catch(Exception e) {
                e.getMessage();
                e.printStackTrace();
            }
        }
    }

    private void setButtons(SendMessage sendMsg) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMsg.setReplyMarkup(replyKeyboardMarkup);

        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboardRowList = new ArrayList<>();
        KeyboardRow firstRow = new KeyboardRow();
        firstRow.add(new KeyboardButton("Miko Ch."));
        keyboardRowList.add(firstRow);
        KeyboardRow secondRow = new KeyboardRow();
        secondRow.add(new KeyboardButton("Korone Ch."));
        keyboardRowList.add(secondRow);

        replyKeyboardMarkup.setKeyboard(keyboardRowList);
    }
}
