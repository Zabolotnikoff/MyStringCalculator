package com.example.mystringcalculator;

import java.util.*;
public class Calculator {
    public static String newFormula; // исходная формула
//"-7/5+236.25 + 237.36 *(-562+565- 1)+ 10 + 2*3 > 5 ? 1 : 2*3 = 731.57"
    private static String formula; // формула очищенная от пробелов

    private static int formulaLangth;  // длина очищенной формулы
    private static int i;  // индекс текущего элемента формулы

    private static Stack operators; // стек для нечисловых значений формулы
    private static Stack numbers; // стек для числовых значений формулы
    private static Stack ternarType; // стек для вложенных тернарных операторов

    public static boolean ERROR; // наличие ошибки в формуле
    public static boolean DIVISION_BY_ZERO; // попытка деления на ноль
    public static boolean EXTRA_DECIMAL_POINT; // лишняя десятичная точка в числе
    public static boolean INVALID_CHARACTER; // недопустимый символ
    public static boolean BRACKETS_MISMATH; // несоответствие скобок


    private static float getFloat (char[] formulaCh) {  // извлечение числового значения из формулы
        String floatS = "";
        int decimalPoint = 0;

        for (; (i < formulaLangth); i++) {
            if (!((formulaCh[i] <= '9' & formulaCh[i] >= '0') | (formulaCh[i] == '.'))) {
                break;
            }
            if (formulaCh[i] == '.') {
                //decimalPoint++;
                if (++decimalPoint > 1) {
                    System.out.println("Лишняя десятичная точка");
                    EXTRA_DECIMAL_POINT = true;
                    ERROR = true;
                    break;
                }
            }
            floatS = floatS + formulaCh[i];
        }
        return Float.parseFloat(floatS);
    }

// обработка операторов и скобок
    private static void getOperation (char operator) {
        char topOperator;

        if (operators.empty()) {  // если стек операторов пуст, помещаем его в стек (в т.ч. тернарный)
            operators.push(operator);
            return;

        } else if (operator == '(') {  // открывающаяся скобка - просто помещаем её в стек
            operators.push(operator);
            return;

        } else if (operator == ')') {  // закрывающаяся скобка - перекидываем всё из стека операторов до парной скобки, скобки удаляем
            while (!operators.empty()) {
                topOperator = (Character)operators.pop();
                if (topOperator == '(' ) {
                    return;
                }
                makeOperation(topOperator);
                if (ERROR) {
                    return;
                }
            }
            System.out.println("Несоответствие скобок");
            BRACKETS_MISMATH = true;
            ERROR = true;
            return;

// !!! Тернарный
        } else if ((operator == '<') | (operator == '>') | (operator == '=')) { // начало тернарного - просто помещаем его в стек
            if (getOperationPriority ((Character)operators.peek()) > getOperationPriority (operator)) {
                makeOperation((Character) operators.pop());
                if (ERROR) {
                    return;
                }
            }
            operators.push(operator);
            return;

//5 = 7>3?5:8 ? 1 : 9
/*            while (!operators.empty()) {
                if (getOperationPriority((Character) operators.peek()) > getOperationPriority(operator)) {
                    makeOperation((Character) operators.pop());
                    if (ERROR) {
                        return;
                    }
                } else {
                    operators.push(operator);
                    return;
                }
                operators.push(operator);
                return;
            }
*/

        } else if (operator == '?') { // продолжение тернарного - перекидываем всё до начала тернарного, оператор помещаем в стек
            while (!operators.empty()) {
                topOperator = (Character)operators.pop();
                if ((topOperator == '<') | (topOperator == '>') | (topOperator == '=')) {
                    operators.push(operator);
                    ternarType.push(topOperator);
                    return;
                }
                makeOperation(topOperator);
                if (ERROR) {
                    return;
                }
            }
            System.out.println("Ошибка в тернарном оператрое");
            ERROR = true;
            return;

        } else if (operator == ':') { // финал тернарного - перекидываем всё до предыдущей части тернарного, оператор помещаем в стек
            while (!operators.empty()) {
                topOperator = (Character)operators.pop();
                if (topOperator == '?') {
                    operators.push(operator);
                    return;
                }
                makeOperation(topOperator);
                if (ERROR) {
                    return;
                }

            }
            System.out.println("Ошибка в тернарном оператрое");
            ERROR = true;
            return;
// Конец тернарного

        } else {  // если стек не пуст, перекидываем всё, пока приоритет больше либо равен приоритету текущего оператора; текущий помещаем в стек
            while (!operators.empty()) {
                if (getOperationPriority ((Character)operators.peek()) >= getOperationPriority (operator)) {
                    makeOperation((Character)operators.pop());
                    if (ERROR) {
                        return;
                    }
                } else {
                    operators.push(operator);
                    return;
                }
            }
            operators.push(operator);
            return;
        }
    }

    // назначаем приоритеты операторам
    private static int getOperationPriority (char operator) {
        switch (operator) {
            case '<':
            case '>':
            case '=':
            case '?':
            case ':':
                return 3;
            case '*':
            case '/':
                return 2;
            case '~':
                return 4;
            case '-':
            case '+':
                return 1;
            case '(':
                return 0;
//			case ')':
//				return -1;
            default:
                System.out.println("Неверный оператор: " + operator);
                ERROR = true;
                return -999999;
        }
    }

    // выполнение арифметических операций, возврат результата в стек с числовыми значениями
    private static void makeOperation (char operator) {
        float f1, f2, f3, f4;
        switch (operator) {
            case '~':
                if (numbers.size() < 1) {
                    System.out.println("Ошибка! Слишком много операторов. ~");
                    ERROR = true;
                    return;
                }
                numbers.push (-(float)numbers.pop());
                break;
            case '*':
                if (numbers.size() < 2) {
                    System.out.println("Ошибка! Слишком много операторов. *");
                    ERROR = true;
                    return;
                }
                numbers.push ((float)numbers.pop() * (float)numbers.pop());
                break;
            case '/':
                if (numbers.size() < 2) {
                    System.out.println("Ошибка! Слишком много операторов. /");
                    ERROR = true;
                    return;
                }
                if ((float)numbers.peek() == 0){
                    System.out.println("Попытка деления на 0");
                    DIVISION_BY_ZERO = true;
                    ERROR = true;
                    return;
                }
                numbers.push (1/(float)numbers.pop() * (float)numbers.pop());
                break;
            case '-':
                if (numbers.size() < 2) {
                    System.out.println("Ошибка! Слишком много операторов. -");
                    ERROR = true;
                    return;
                }
                numbers.push (-(float)numbers.pop() + (float)numbers.pop());
                break;
            case '+':
                if (numbers.size() < 2) {
                    System.out.println("Ошибка! Слишком много операторов. +");
                    ERROR = true;
                    return;
                }
                numbers.push ((float)numbers.pop() + (float)numbers.pop());
                break;
// Тернарный
            case ':':
                if (numbers.size() < 4) {
                    System.out.println("Ошибка!");
                    ERROR = true;
                    return;
                }
                f4 = (float)numbers.pop();
                f3 = (float)numbers.pop();
                f2 = (float)numbers.pop();
                f1 = (float)numbers.pop();
                switch ((Character)ternarType.pop()) {
                    case '<':
                        numbers.push (f1 < f2 ? f3 : f4);
                        break;
                    case '>':
                        numbers.push (f1 > f2 ? f3 : f4);
                        break;
                    case '=':
                        numbers.push (f1 == f2 ? f3 : f4);
                        break;
                }
                break;
            case '<':
            case '>':
            case '=':
            case '?':
                System.out.println("Ошибка в тернарном операторе: " + operator);
                ERROR = true;
                return;
// конец тернарного
            case ')':
            case '(':
                System.out.println("Несоответствие скобок");
                BRACKETS_MISMATH = true;
                ERROR = true;
                return;

            default: // сюда, по идее, программа никогда не придёт
                System.out.println("Ошибка в формуле!!: " + operator);
                ERROR = true;
                return;
        }
        return;
    }

    public static float getResult() {  //
        formula = newFormula.replaceAll("\\s","");  // очищаем формулу
        formulaLangth = formula.length();
        operators = new Stack();
        numbers = new Stack();
        ternarType = new Stack();
        i = 0;
        ERROR = false;
        DIVISION_BY_ZERO = false;
        EXTRA_DECIMAL_POINT = false;
        INVALID_CHARACTER = false;
        BRACKETS_MISMATH = false;

        char[] formulaCh = formula.toCharArray();  // очищенную формулу преобразовываем в символьный массив

        while ((i < formulaLangth) & !ERROR) {  // перебираем все символы строки формул
            switch (formulaCh[i]) {
                case  '0':
                case  '1':
                case  '2':
                case  '3':
                case  '4':
                case  '5':
                case  '6':
                case  '7':
                case  '8':
                case  '9':
                    numbers.push(getFloat (formulaCh)); // выделяем числа
                    break;
                case '-':  // выявляем унарный минус, заменяем его знак на ~
                    if (i == 0) {
                        formulaCh[i] = '~';
                    } else {
                        switch (formulaCh[i-1]) {
                            case '(':
                            case '<':
                            case '>':
                            case '=':
                            case '?':
                            case ':':
                                formulaCh[i] = '~';
                        }
//                        if (formulaCh[i-1] == '(') formulaCh[i] = '~';
                    }
                case ')':
                    if (i > 0) {
                        switch (formulaCh[i-1]) {
                            case '~':
                            case '+':
                            case '-':
                            case '*':
                            case '/':
                                ERROR = true;
                                return -7777777;
                        }
                    }
                case '+':
                case '*':
                case '/':
                case '(':
                    getOperation (formulaCh[i++]);
                    break;
// Тернарный.  Можно объединить с предыдущим блоком. Но версию без тернарного не сохранил, поэтому, на всякий случай, отделил его
                case '<':
                case '>':
                case '=':
                case '?':
                case ':':
                    getOperation (formulaCh[i++]);
                    break;
// Конец тернарного
                default:
                    System.out.println("Недопустимый символ в формуле: " + formulaCh[i] + " под номером " + (i + 1));
                    INVALID_CHARACTER = true;
                    ERROR = true;
                    return -999999;
            } //switch
        } //while

        while ((!operators.empty()) & !ERROR) { // выполняем оставшиеся в стеке операторы
            makeOperation((Character) operators.pop());
        }

        if (numbers.size() != 1) { // проверяем количество числовых значений в стеке - если не одно, то ошибка
            ERROR = true;
        }

        if (ERROR) {
            return -8888888;
        } else {
            return (float)numbers.pop();
        }
    }
}
