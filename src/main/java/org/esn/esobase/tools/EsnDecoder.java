/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.tools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author scraelos
 */
public class EsnDecoder {

    private static final Pattern esnEncodingPattern = Pattern.compile("^.*[ÀÁÂÄÆÇÈÉÊÌÍÎÏÒÓÑŒÙÚÅŸÖËÔÜàáâäæçèéêìíîïòóñœùúûåÿöëôü]+.*");
    private static final Pattern enEncodingPattern = Pattern.compile("[a-zA-Z]");
    private static final Pattern ruPattern = Pattern.compile("[а-яА-я]");

    public static boolean IsEsnEncoding(String text) {
        boolean result = false;
        int enCounter = 0;
        int ruCounter = 0;
        Matcher enMatcher = enEncodingPattern.matcher(text);
        while (enMatcher.find()) {
            enCounter++;
        }
        Matcher matcher = esnEncodingPattern.matcher(text);
        while (matcher.find()) {
            ruCounter++;
        }
        if (ruCounter > enCounter) {
            result = true;
        }
        return result;
    }
    
    public static boolean IsRu(String text) {
        boolean result = false;
        int enCounter = 0;
        int ruCounter = 0;
        Matcher enMatcher = enEncodingPattern.matcher(text);
        while (enMatcher.find()) {
            enCounter++;
        }
        Matcher matcher = ruPattern.matcher(text);
        while (matcher.find()) {
            ruCounter++;
        }
        if (ruCounter > enCounter) {
            result = true;
        }
        return result;
    }

    public static String encode(String input) {
        input = input.replaceAll("А", "A");
        input = input.replaceAll("Б", "À");
        input = input.replaceAll("В", "Á");
        input = input.replaceAll("Г", "Â");
        input = input.replaceAll("Д", "Ä");
        input = input.replaceAll("Е", "E");
        input = input.replaceAll("Ё", "E");
        input = input.replaceAll("Ж", "Æ");
        input = input.replaceAll("З", "Ç");
        input = input.replaceAll("И", "È");
        input = input.replaceAll("Й", "É");
        input = input.replaceAll("К", "Ê");
        input = input.replaceAll("Л", "Ì");
        input = input.replaceAll("М", "Í");
        input = input.replaceAll("Н", "Î");
        input = input.replaceAll("О", "O");
        input = input.replaceAll("П", "Ï");
        input = input.replaceAll("Р", "P");
        input = input.replaceAll("С", "C");
        input = input.replaceAll("Т", "Ò");
        input = input.replaceAll("У", "Ó");
        input = input.replaceAll("Ф", "Ñ");
        input = input.replaceAll("Х", "X");
        input = input.replaceAll("Ц", "Œ");
        input = input.replaceAll("Ч", "Ù");
        input = input.replaceAll("Ш", "Ú");
        input = input.replaceAll("Щ", "Û");
        input = input.replaceAll("Ъ", "Å");
        input = input.replaceAll("Ы", "Ÿ");
        input = input.replaceAll("Ь", "Ö");
        input = input.replaceAll("Э", "Ë");
        input = input.replaceAll("Ю", "Ô");
        input = input.replaceAll("Я", "Ü");
        input = input.replaceAll("а", "a");
        input = input.replaceAll("б", "à");
        input = input.replaceAll("в", "á");
        input = input.replaceAll("г", "â");
        input = input.replaceAll("д", "ä");
        input = input.replaceAll("е", "e");
        input = input.replaceAll("ё", "e");
        input = input.replaceAll("ж", "æ");
        input = input.replaceAll("з", "ç");
        input = input.replaceAll("и", "è");
        input = input.replaceAll("й", "é");
        input = input.replaceAll("к", "ê");
        input = input.replaceAll("л", "ì");
        input = input.replaceAll("м", "í");
        input = input.replaceAll("н", "î");
        input = input.replaceAll("о", "o");
        input = input.replaceAll("п", "ï");
        input = input.replaceAll("р", "p");
        input = input.replaceAll("с", "c");
        input = input.replaceAll("т", "ò");
        input = input.replaceAll("у", "ó");
        input = input.replaceAll("ф", "ñ");
        input = input.replaceAll("х", "x");
        input = input.replaceAll("ц", "œ");
        input = input.replaceAll("ч", "ù");
        input = input.replaceAll("ш", "ú");
        input = input.replaceAll("щ", "û");
        input = input.replaceAll("ъ", "å");
        input = input.replaceAll("ы", "ÿ");
        input = input.replaceAll("ь", "ö");
        input = input.replaceAll("э", "ë");
        input = input.replaceAll("ю", "ô");
        input = input.replaceAll("я", "ü");
        return input;
    }

    public static String decode(String input) {
        input = input.replaceAll("A", "А");
        input = input.replaceAll("À", "Б");
        input = input.replaceAll("Á", "В");
        input = input.replaceAll("Â", "Г");
        input = input.replaceAll("Ä", "Д");
        input = input.replaceAll("E", "Е");
        //input = input.replaceAll("E", "Ё");
        input = input.replaceAll("Æ", "Ж");
        input = input.replaceAll("Ç", "З");
        input = input.replaceAll("È", "И");
        input = input.replaceAll("É", "Й");
        input = input.replaceAll("Ê", "К");
        input = input.replaceAll("Ì", "Л");
        input = input.replaceAll("Í", "М");
        input = input.replaceAll("Î", "Н");
        input = input.replaceAll("O", "О");
        input = input.replaceAll("Ï", "П");
        input = input.replaceAll("P", "Р");
        input = input.replaceAll("C", "С");
        input = input.replaceAll("Ò", "Т");
        input = input.replaceAll("Ó", "У");
        input = input.replaceAll("Ñ", "Ф");
        input = input.replaceAll("²", "Ф");
        input = input.replaceAll("X", "Х");
        input = input.replaceAll("Œ", "Ц");
        input = input.replaceAll("Ù", "Ч");
        input = input.replaceAll("Ú", "Ш");
        input = input.replaceAll("Û", "Щ");
        input = input.replaceAll("Å", "Ъ");
        input = input.replaceAll("µ", "Ъ");
        input = input.replaceAll("Ÿ", "Ы");
        input = input.replaceAll("Ö", "Ь");
        input = input.replaceAll("Ë", "Э");
        input = input.replaceAll("Ô", "Ю");
        input = input.replaceAll("Ü", "Я");
        input = input.replaceAll("a", "а");
        input = input.replaceAll("à", "б");
        input = input.replaceAll("á", "в");
        input = input.replaceAll("â", "г");
        input = input.replaceAll("ä", "д");
        input = input.replaceAll("e", "е");
        //input = input.replaceAll("e", "ё");
        input = input.replaceAll("æ", "ж");
        input = input.replaceAll("ç", "з");
        input = input.replaceAll("è", "и");
        input = input.replaceAll("é", "й");
        input = input.replaceAll("ê", "к");
        input = input.replaceAll("ì", "л");
        input = input.replaceAll("í", "м");
        input = input.replaceAll("î", "н");
        input = input.replaceAll("o", "о");
        input = input.replaceAll("ï", "п");
        input = input.replaceAll("p", "р");
        input = input.replaceAll("c", "с");
        input = input.replaceAll("ò", "т");
        input = input.replaceAll("ó", "у");
        input = input.replaceAll("ñ", "ф");
        input = input.replaceAll("³", "ф");
        input = input.replaceAll("x", "х");
        input = input.replaceAll("œ", "ц");
        input = input.replaceAll("ù", "ч");
        input = input.replaceAll("ú", "ш");
        input = input.replaceAll("û", "щ");
        input = input.replaceAll("å", "ъ");
        input = input.replaceAll("°", "ъ");
        input = input.replaceAll("ÿ", "ы");
        input = input.replaceAll("ö", "ь");
        input = input.replaceAll("ë", "э");
        input = input.replaceAll("ô", "ю");
        input = input.replaceAll("ü", "я");

        return input;
    }
}
