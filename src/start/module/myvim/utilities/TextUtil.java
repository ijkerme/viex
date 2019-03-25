package start.module.myvim.utilities;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * Part codes of this file from 
 * @author jker
 */
public class TextUtil {

    private static final Pattern U_PATTERN = Pattern.compile("\\\\u[0-9a-fA-F]{2,4}");

    //<editor-fold defaultstate="collapsed" desc="Html escape chars">
    public static final String[][] HTML_ESCAPE_CHARS = {
          { "&lt;", "<" }, {
            "&gt;", ">" }, {
            "&amp;", "&" }, {
            "&quot;", "\"" }, {
            "&agrave;", "à" }, {
            "&Agrave;", "À" }, {
            "&acirc;", "â" }, {
            "&auml;", "ä" }, {
            "&Auml;", "Ä" }, {
            "&Acirc;", "Â" }, {
            "&aring;", "å" }, {
            "&Aring;", "Å" }, {
            "&aelig;", "æ" }, {
            "&AElig;", "Æ" }, {
            "&ccedil;", "ç" }, {
            "&Ccedil;", "Ç" }, {
            "&eacute;", "é" }, {
            "&Eacute;", "É" }, {
            "&aacute;", "á" }, {
            "&Aacute;", "Á" }, {
            "&egrave;", "è" }, {
            "&Egrave;", "È" }, {
            "&ecirc;", "ê" }, {
            "&Ecirc;", "Ê" }, {
            "&euml;", "ë" }, {
            "&Euml;", "Ë" }, {
            "&iuml;", "ï" }, {
            "&Iuml;", "Ï" }, {
            "&iacute;", "í" }, {
            "&Iacute;", "Í" }, {
            "&atilde;", "ã" }, {
            "&Atilde;", "Ã" }, {
            "&otilde;", "õ" }, {
            "&Otilde;", "Õ" }, {
            "&oacute;", "ó" }, {
            "&Oacute;", "Ó" }, {
            "&ocirc;", "ô" }, {
            "&Ocirc;", "Ô" }, {
            "&ouml;", "ö" }, {
            "&Ouml;", "Ö" }, {
            "&oslash;", "ø" }, {
            "&Oslash;", "Ø" }, {
            "&szlig;", "ß" }, {
            "&ugrave;", "ù" }, {
            "&Ugrave;", "Ù" }, {
            "&uacute;", "ú" }, {
            "&Uacute;", "Ú" }, {
            "&ucirc;", "û" }, {
            "&Ucirc;", "Û" }, {
            "&uuml;", "ü" }, {
            "&Uuml;", "Ü" }, {
            "&nbsp;", " " }, {
            "&reg;", "\u00AE" }, {
            "&copy;", "\u00A9" }, {
            "&euro;", "\u20A0" }, {
            "&#8364;", "\u20AC" }

    };
    //</editor-fold>

    public static String getHtmlEntity(char ch) {
        switch (ch) {
        case '<':
            return "&lt;";
        case '>':
            return "&gt;";
        case '&':
            return "&amp;";
        case '"':
            return "&quot;";
        case 'à':
            return "&agrave;";
        case 'á':
            return "&aacute;";
        case 'À':
            return "&Agrave;";
        case 'Á':
            return "&Aacute;";
        case 'â':
            return "&acirc;";
        case 'Â':
            return "&Acirc;";
        case 'ä':
            return "&auml;";
        case 'Ä':
            return "&Auml;";
        case 'å':
            return "&aring;";
        case 'Å':
            return "&Aring;";
        case 'ã':
            return "&atilde;";
        case 'Ã':
            return "&Atilde;";
        case 'æ':
            return "&aelig;";
        case 'Æ':
            return "&AElig;";
        case 'ç':
            return "&ccedil;";
        case 'Ç':
            return "&Ccedil;";
        case 'é':
            return "&eacute;";
        case 'É':
            return "&Eacute;";
        case 'è':
            return "&egrave;";
        case 'È':
            return "&Egrave;";
        case 'ê':
            return "&ecirc;";
        case 'Ê':
            return "&Ecirc;";
        case 'ë':
            return "&euml;";
        case 'Ë':
            return "&Euml;";
        case 'í':
            return "&iacute;";
        case 'Í':
            return "&Iacute;";
        case 'ï':
            return "&iuml;";
        case 'Ï':
            return "&Iuml;";
        case 'õ':
            return "&otilde;";
        case 'Õ':
            return "&Otilde;";
        case 'ó':
            return "&oacute;";
        case 'ô':
            return "&ocirc;";
        case 'Ó':
            return "&Oacute;";
        case 'Ô':
            return "&Ocirc;";
        case 'ö':
            return "&ouml;";
        case 'Ö':
            return "&Ouml;";
        case 'ø':
            return "&oslash;";
        case 'Ø':
            return "&Oslash;";
        case 'ß':
            return "&szlig;";
        case 'ù':
            return "&ugrave;";
        case 'Ù':
            return "&Ugrave;";
        case 'ú':
            return "&uacute;";
        case 'Ú':
            return "&Uacute;";
        case 'û':
            return "&ucirc;";
        case 'Û':
            return "&Ucirc;";
        case 'ü':
            return "&uuml;";
        case 'Ü':
            return "&Uuml;";
        case '\u00AE':
            return "&reg;";
        case '\u00A9':
            return "&copy;";
        case '\u20A0':
            return "&euro;";
        case '\u20AC':
            return "&#8364;";
        default: {
            int ci = 0xffff & ch;
            if (ci < 160) {
                return null;
            }
            return "&#" + ci + ";";
        }
        }
    }

    public static String native2Ascii(String s) {
        if (s == null)
            return "";
        StringBuffer sb = new StringBuffer(s.length() + 80);
        for (int i = 0, len = s.length(); i < len; i++) {
            char c = s.charAt(i);
            if (c <= 0xff) {
                sb.append(c);
            }
            else {
                sb.append("\\u" + Integer.toHexString((int)c).toUpperCase());
            }
        }
        return sb.toString();
    }

    public static String ascii2Native(String s) {
        StringBuffer ret = new StringBuffer();
        Matcher matcher = U_PATTERN.matcher(s);
        while (matcher.find()) {
            try {
                String uValue = matcher.group().substring(2);
                String nValue = new String(new char[]{(char) Integer.parseInt(uValue, 16)});
                matcher.appendReplacement(ret, nValue);
            } catch (NumberFormatException t) {
            }
        }
        matcher.appendTail(ret);
        return ret.toString();
    }

}
