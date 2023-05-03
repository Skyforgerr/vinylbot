package com.shimadsy.bot;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;

public class Menu {
	private ReplyKeyboardMarkup replyKeyboardMarkup;
	
	public Menu() {
		replyKeyboardMarkup = new ReplyKeyboardMarkup();
	       replyKeyboardMarkup.setSelective(true);
	       replyKeyboardMarkup.setResizeKeyboard(true);
	       replyKeyboardMarkup.setOneTimeKeyboard(false);

	       KeyboardRow row1 = new KeyboardRow();
	       KeyboardButton listOfVinyls = new KeyboardButton();
	       listOfVinyls.setText("Вывести список всех пластинок");
	       row1.add(listOfVinyls);

	       KeyboardRow row2 = new KeyboardRow();
	       KeyboardButton vinylInfo = new KeyboardButton();
	       vinylInfo.setText("Вывести информацию о конкретной пластинке (введите название)");
	       row2.add(vinylInfo);

	       KeyboardRow row3 = new KeyboardRow();
	       KeyboardButton addNewVinyl = new KeyboardButton();
	       addNewVinyl.setText("Добавить новую пластинку");
	       row3.add(addNewVinyl);
	}

}
