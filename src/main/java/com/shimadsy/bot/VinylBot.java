package com.shimadsy.bot;

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
        Statement chatStatement = connection.createStatement();
        chatStatement.executeUpdate("INSERT INTO chat_logs (id, message) VALUES ('" + message.getChatId() + "', '" + message.getText() + "');");
        String command = message.getText();
        Long chatId = message.getChatId();

        if (command.equals("/get_vinyls")){         //вывод списка всех пластинок
            Statement statement = connection.createStatement();
            String result = "";
            ResultSet resultSet = statement.executeQuery("SELECT name FROM vinyl");
            while (resultSet.next()){
                result = result + "\n" + resultSet.getString("name");
            }
            execute(SendMessage.builder().chatId(message.getChatId().toString()).text(result).build());
            chatStatement.executeUpdate("INSERT INTO chat_logs (id, message) VALUES ('" + message.getChatId() + "', '" + result + "');");
        }else if (command.equals("/get_info")){     //вывод информации об одной пластинке
            states.put(chatId, "info");
            execute(SendMessage.builder().chatId(chatId.toString()).text("Введите название пластинки для вывода дополнительной информации:").build());
            chatStatement.executeUpdate("INSERT INTO chat_logs (id, message) VALUES ('" + message.getChatId() + "', '" + "Введите название пластинки для вывода дополнительной информации:" + "');");
        }else if (command.equals("/add_vinyl")) {   //добавление пластинки
            states.put(chatId, "add");
            execute(SendMessage.builder().chatId(message.getChatId().toString()).text("Введите через запятую название, описание, цену, год, лейбл").build());
            chatStatement.executeUpdate("INSERT INTO chat_logs (id, message) VALUES ('" + message.getChatId() + "', '" + "Введите через запятую id, название, описание, цену, год, лейбл" + "');");
        }else if(states.containsKey(chatId)){
            Update update = new Update();
            if (states.get(chatId).equals("info")){
                execute(SendMessage.builder().chatId(message.getChatId().toString()).text(searchForVinyl(message.getText(), message)).build());
                states.remove(chatId);
            }else if (states.get(chatId).equals("add")){
                addVinyl(message.getText());
                execute(SendMessage.builder().chatId(chatId.toString()).text("Новая пластинка добавлена").build());
                chatStatement.executeUpdate("INSERT INTO chat_logs (id, message) VALUES ('" + message.getChatId() + "', '" + "Новая пластинка добавлена" + "');");
                states.remove(chatId);
            }
        }else{
            execute(SendMessage.builder().chatId(message.getChatId().toString()).text("Неизвестная команда").build());
        }
    }

    public String searchForVinyl(String name, Message message) throws SQLException {
        Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM vinyl WHERE name=?");
        preparedStatement.setString(1, message.getText());
        System.out.println(preparedStatement);
        String result = "";
        ResultSet resultSet1 = preparedStatement.executeQuery();
        while (resultSet1.next()){
            result = "Id: "+ resultSet1.getString("id")
                    + ", описание: "
                    + resultSet1.getString("description")
                    + ", стоимость: "
                    + resultSet1.getString("cost")
                    + ", год: "
                    + resultSet1.getString("year")
                    + ", лейбл: "
                    + resultSet1.getString("lable");
        }
        System.out.println(result);
        return result;
    }

    private void addVinyl(String text) throws SQLException {
        Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        String[] splitted = text.split(", ");
        Statement statement = connection.createStatement();
        statement.executeUpdate("INSERT INTO vinyl (id, name, description, cost, year, lable) VALUES('" + Integer.valueOf(splitted[0]) + "', '" + splitted[1] + "','"
                + splitted[2] + "','"
                + Integer.valueOf(splitted[3]) + "','"
                + Integer.valueOf(splitted[4]) + "','"
                + splitted[5] + "');");
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
