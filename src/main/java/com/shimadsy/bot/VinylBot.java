package com.shimadsy.bot;

import com.shimadsy.bot.entity.Vinyl;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.List;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import org.springframework.beans.factory.annotation.Autowired;

public class VinylBot extends TelegramLongPollingBot {
    private final String BOT_TOKEN = "6099074254:AAHEDwiAg7taJX3LOdP1USvkQE9-sJsxKRI";
    private final String BOT_USERNAME = "@NewVinylShopbot";
    private final String DB_URL = "jdbc:postgresql://localhost:5432/vinylbot";
    private final String DB_USER = "postgres";
    private final String DB_PASSWORD = "qwertyps4";

    private Map<Long, String> states = new HashMap<>();
	
    @Override
    public void onUpdateReceived(Update update){
        if(update.hasMessage()) {
            try {
                handleMessage(update.getMessage());
            } catch (TelegramApiException | SQLException e) {
                e.printStackTrace();
            }
        }	
    }
    
    private void handleMessage(Message message) throws TelegramApiException, SQLException {
        Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        if(message.hasText() && message.hasEntities()){
            Optional<MessageEntity> commandEntity = message.getEntities().stream().filter(e -> "bot_command".equals(e.getType())).findFirst();
            if (commandEntity.isPresent()){
                String command = message.getText().substring(commandEntity.get().getOffset(), commandEntity.get().getLength());
                Long chatId = message.getChatId();

                if (command.equals("/get_vinyls")){
                    Statement statement = connection.createStatement();
                    String result = "";
                    ResultSet resultSet = statement.executeQuery("SELECT name FROM vinyl");
                    while (resultSet.next()){
                        result = result + "\n" + resultSet.getString("name");
                    }
                    execute(SendMessage.builder().chatId(message.getChatId().toString()).text(result).build());
                }else if (command.equals("/get_info")){
                    states.put(chatId, "info");
                    execute(SendMessage.builder().chatId(chatId.toString()).text("Введите название пластинки для вывода дополнительной информации:").build());
                }else if (command.equals("/add_vinyl")) {
                    states.put(chatId, "add");
                    execute(SendMessage.builder().chatId(message.getChatId().toString()).text("Вы выбрали add_vinyl").build());
                }else if(states.containsKey(chatId)){
                    Update update = new Update();
                    if (states.get(chatId).equals("info")){
                        Statement statement1 = connection.createStatement();
                        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM vinyl WHERE name=?");
                        preparedStatement.setString(1, update.getMessage().getText());
                        System.out.println(preparedStatement);
                        ResultSet resultSet1 = preparedStatement.executeQuery();
                        System.out.println(resultSet1);
                        execute(SendMessage.builder().chatId(message.getChatId().toString()).text(String.valueOf(resultSet1)).build());
                        states.remove(chatId);
                    }else if (states.get(chatId).equals("add")){
                        addVinyl(update.getMessage().getText());
                        execute(SendMessage.builder().chatId(chatId.toString()).text("Новая пластинка добавлена").build());
                        states.remove(chatId);
                    }
                }else{
                    execute(SendMessage.builder().chatId(message.getChatId().toString()).text("Неизвестная команда").build());
                }
            }
        }
    }

    private void addVinyl(String text) throws SQLException {
        Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        String[] splitted = text.split(",");
        
    }


    @Override
    public String getBotToken() {
	return BOT_TOKEN;
    }

    @Override
    public String getBotUsername() {
	return BOT_USERNAME;
    }
	
    public static void main(String[] args) throws TelegramApiException {
	VinylBot bot = new VinylBot();
	TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
	telegramBotsApi.registerBot(bot);
    }

}
