package week3;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Calculator_modularize_2 {

	static int index;

	static enum type {NUMBER, PLUS, MINUS, STAR, SLASH };

	/**
	 * tokeonクラス
	 */
	static class Token {
	    type   token_type;    /* tokenのtype NUMBER/PLUS/MINUS */
	    double number;         /* 値のみ --- 数値 */

	    /**
	     * token コンストラクタ
	     * @param a NUMBER/PLUS/MINUS
	     * @param b aがNUMBERのとき、値
	     */
	    Token(type a, double b) {
	    	this.token_type = a;
	    	this.number = b;
	    }

	    Token(type a) {
	    	this.token_type = a;
	    	this.number = 0;
	    }
	  }


	public static void main(String args[])
	{

		/* テスト実行 */
		runTest();

		/*
		 * 標準入力から入力された計算式を取り込む
		 */
        System.out.print("> ");
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);
        try{

        	String line = br.readLine();

        	/*
        	 * 入力された計算式を、トークンに分解する
        	 */
        	ArrayList<Token> tokens = tokenize(line);

        	if(tokens != null) {
                /*
                 * 分解されたトークンを評価して計算する
                 */
                double eval_answer = evaluate(tokens);

            	/*
            	 * 計算結果を標準出力に出力する
            	 */
                System.out.println("answer = " + eval_answer);
        	}

        }catch(Exception e){
        	e.printStackTrace();
        }

	}

	/**
	 * テスト実行
	 */
	private static void runTest() {
        System.out.println("==== Test started! ====");
		test("1+2",3);               // 単一の＋のみ
		test("3-1",2);               // 単一の－のみ、解が正の数となる場合
		test("2-5",-3);              // 単一の－のみ、解が負の数となる場合
		test("1.0+2.1-3",0.1);       // 小数点を含む数と含まない数
		test("1+2+3",6);             // 複数の＋
		test("1+2-3",0);             // ＋と－
		test("1-3+2",0);             // －と＋
		test("10-2-3",5);            // 複数の－

		test("3*2",6);               // 単一の積のみ
		test("15/3",5);              // 単一の商のみ
		test("1*2*3",6);             // 複数の積
		test("27/9*3",9);            // 商と積
		test("27*3/9",9);            // 積と商
		test("6/3/2",1);             // 複数の商

		test("2*3+1",7);             // 単一の積と＋
		test("2*3-1",5);             // 単一の積と－
		test("6/2+1",4);             // 単一の商と＋
		test("6/2-1",2);             // 単一の商と－

		test("1+2*3",7);             // ＋と単一の積
		test("1-2*3",-5);            // －と単一の積
		test("1+6/2",4);             // ＋と単一の商
		test("1-6/2",-2);            // －と単一の商

		test("1*2*3+1",7);           // 複数の積と＋
		test("1*2*3-1",5);           // 複数の積と－
		test("12/3/4+1",2);          // 複数の商と＋
		test("12/3/4-1",0);          // 複数の商と－

		test("1+1*2*3",7);           // ＋と複数の積
		test("1-1*2*3",-5);          // －と複数の積
		test("1+12/3/4",2);          // ＋と複数の商
		test("1-12/3/4",0);          // －と複数の商

		test("10*10+10/5",102);      // 単一の積と商の＋
		test("10*10-10/5",98);       // 単一の積と商の－
		test("0.1*0.1+2.5/0.5",5.01);// 小数点以下の数の認識
		test("3*3*3+2*2*2",35);      // 複数の積の＋
		test("27/3/3-1*1*1",2);      // 複数の積と商の－
		test("27/1/1-9/3/3",26);     // 複数の商の－
		test("1+2*2-3/3",4);         // 積と商の＋と－
		test("1 + 2 + 3",6);         // スペースを読み飛ばすかの確認
		test("1/3",0.3333333);       // 1e-8 = 0.00000001(小数点以下8桁)なので、比較対象が小数点以下7桁のこの列はFAILとなる
		test("1/3",0.33333333);      // 1e-8 = 0.00000001(小数点以下8桁)なので、比較対象が小数点以下8桁のこの列はPASSとなる
        System.out.println("==== Test finished! ====");
	}

	/**
	 * テスト用モジュール
	 * @param line
	 * @param expectedAnswer
	 */
	private static void test(String line, double expectedAnswer) {
    	ArrayList<Token> tokens = tokenize(line);
    	if (tokens != null) {
    		double actualAnswer = evaluate(tokens);

    		if(Math.abs(actualAnswer - expectedAnswer) < 1e-8) {
                System.out.println("PASS! (" + line + " = " + expectedAnswer + ")");
    		}
    		else {
                System.out.println("FAIL! (" + line + " should be " + expectedAnswer + " but was " + actualAnswer);
    		}
    	}
	}



	/*
	 * 文字列をtokenに分解する
	 */
	private static ArrayList<Token> tokenize(String line){

		boolean flgError = false;
		Token t;
		index = 0;

		ArrayList<Token> tokens = new ArrayList<Token>();

		/* (ここを追加)
		 * トークンのArrayListの先頭に、+記号のトークンをあらかじめセットしておく。
		 */
		tokens.add( new Token(type.PLUS));

		/*
		 * 文字列を記号と数字に分解してトークンのArrayListにセットする。
		 */
		while ( index < line.length() ){

			String ch = line.substring(index, index + 1);

			t = null;   /* ここも追加 */

			if (isNumber(ch)) {
				t = readNumber(line);
			}
			else if (ch.equals("+")) {
				t = readPlus();
			}
			else if (ch.equals("-")) {
				t = readMinus();
			}
			else if (ch.equals("*")) {
				t = readStar();
			}
			else if (ch.equals("/")) {
				t = readSlash();
			}

			/* 追加 */
			else if (ch.equals(" ")) {
				index++;	/* スペースを読み飛ばす */
			}
			else {
				/* 計算できないものを検出したので、エラーメッセージを表示して終了 */
				System.out.println("Invalid character found: " + ch);
				flgError = true;
				break;
			}

			if (t != null) {
				tokens.add(t);
			}

		}

		if (flgError){
			return null;
		}

		return tokens;

	}

	/**
	 * 数字か否かの判別
	 * @param val
	 * @return valが数字のときはtrue, 数字でない場合はfalse
	 */
	private static boolean isNumber(String val) {
		try {
			Integer.parseInt(val);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	/**
	 * 数字の読み込み
	 * @param line
	 * @return
	 */
	private static Token readNumber(String line) {
		double numValue = 0;

		/* 整数部分の読み込み */
		while (index < line.length() &&
				isNumber(line.substring(index, index + 1))) {

			numValue = numValue * 10 + Integer.parseInt(line.substring(index, index + 1));
			index++;
		}
		if (index < line.length() &&
			".".equals(line.substring(index, index + 1))) {
			index++;
			double keta = 0.1;
			/* 小数部分の読み込み*/
			while (index < line.length() &&
					isNumber(line.substring(index, index + 1))) {
				numValue += Integer.parseInt(line.substring(index, index + 1)) * keta;
				keta *= 0.1;
				index++;
			}
		}
		Token t = new Token(type.NUMBER, numValue);
		return t;
	}

	/**
	 * + 記号の読み込み
	 * @return PLUS記号のトークン
	 */
	private static Token readPlus() {
		Token t = new Token(type.PLUS);
		index++;
		return t;
	}

	/**
	 * - 記号の読み込み
	 * @return MINUS記号のトークン
	 */
	private static Token readMinus() {
		Token t = new Token(type.MINUS);
		index++;
		return t;
	}

	/**
	 * * 記号の読み込み
	 * @return STAR記号のトークン
	 */
	private static Token readStar() {
		Token t = new Token(type.STAR);
		index++;
		return t;
	}

	/**
	 * / 記号の読み込み
	 * @return SLASH記号のトークン
	 */
	private static Token readSlash() {
		Token t = new Token(type.SLASH);
		index++;
		return t;
	}

	/**
	 * 計算した結果を返す
	 * @param tokens
	 * @return 計算結果
	 */
	private static double evaluate(ArrayList<Token> tokens) {

		double answer = 0;

		/*
		 * *記号と/記号の処理
		 *     +2*3-8/4
		 *  → +0+6-0-2  というように置き換える
		 */
		for (int i = 3; i< tokens.size(); i++) {

			Token t = tokens.get(i);

			if (t.token_type == type.NUMBER) {

				Token t_ope = tokens.get(i-1);
				Token t_pre_num = tokens.get(i-2);
				Token t_pre_ope = tokens.get(i-3);

				if (t_pre_num.token_type == type.NUMBER) {
					if (t_ope.token_type == type.STAR) {
						t.number = t_pre_num.number * t.number;
						t_ope.token_type = t_pre_ope.token_type;
						t_pre_num.number = 0;
					}
					else if (t_ope.token_type == type.SLASH) {
						if (t.number != 0) {
							t.number = t_pre_num.number / t.number;
							t_ope.token_type = t_pre_ope.token_type;
							t_pre_num.number = 0;
						}
						else {
							System.out.println("Invalid syntax");
						}
					}
				}
			}
		}

		/*
		 * +記号と-記号の処理
		 */
		for (int i = 1; i < tokens.size(); i++) {

			Token t = tokens.get(i);
			if (t.token_type == type.NUMBER) {
				Token pre_t = tokens.get(i-1);
				if (pre_t.token_type == type.PLUS) {
					answer += t.number;
				}
				else if (pre_t.token_type == type.MINUS) {
					answer -= t.number;
				}
				else {
					System.out.println("Invalid syntax");
				}
			}
		}
		return answer;
	}

}