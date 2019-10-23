package com.taomee.tms.mgr.tools;


import com.alibaba.dubbo.common.utils.Stack;

public class ExprProcessTools {

/**
	 * @brief 将字符数组转化为Int数组
	 * @param 字符数组
	 */
	public static int[] convertStringToInt(String[] strs) {
		int[] result = new int[strs.length];
		for (int i = 0; i < result.length; ++i) {
			result[i] = Integer.valueOf(strs[i]);
		}
		return result;
	}
	
	/** 
	 * 将字符数组转化为Interger数组
	 * @param 字符数组
	 */
	public static Integer[] convertStringsToIntegers(String[] strs) {
		Integer[] result = new Integer[strs.length];
		for (int i = 0; i < result.length; ++i) {
			result[i] = Integer.valueOf(strs[i]);
		}
		return result;
	}

	/**
	 * 将中缀表达式转化为后缀表达式,并直接求出值
	 * @param 中缀表达式字符串
	 */
	public static Double convertToSuffixExpression(String expr) {
		
        Stack<Double> numbersStack = new Stack<Double>();  
        Stack<Character> operatorsStack = new Stack<Character>();  
        Stack<Object> suffixExpr = new Stack<Object>();  
        int len = expr.length();  
        char c, temp;  
        double number;  
        for (int i = 0; i < len; i++) {  
            c = expr.charAt(i);  
            if (Character.isDigit(c)) {  
                int endDigitPos = getEndPosOfDigit(expr, i);  
                number = Double.parseDouble(expr.substring(i, endDigitPos));
               // System.out.println("end:" + endDigitPos);
               //  System.out.println("number: " + number);
                i = endDigitPos - 1;  
                numbersStack.push(number);  
                if ((int)number == number) {  
                    suffixExpr.push((int)number);  
                } else {  
                    suffixExpr.push(number);  
                }  
            } else if (isOperator(c)) {  
                // 操作符栈非空，且栈顶不是'('，且当前操作符优先级低于栈顶操作符  
                while (!operatorsStack.isEmpty()   
                        && operatorsStack.peek() != '('   
                        && priorityCompare(c, operatorsStack.peek()) <= 0) {  
                    suffixExpr.push(operatorsStack.peek());  
                    numbersStack.push(calc(numbersStack, operatorsStack.pop()));  
                }  
                operatorsStack.push(c);  
            } else if (c == '(') {  
                operatorsStack.push(c);  
            } else if (c == ')') {  
                while ((temp = operatorsStack.pop()) != '(') {  
                    numbersStack.push(calc(numbersStack, temp));  
                    suffixExpr.push(temp);  
                }  
            } else if (c == ' ') {  
                  
            } else {  
                throw new IllegalArgumentException("wrong character '" + c + "'");  
            }  
        }  
          
        while (!operatorsStack.isEmpty()) {  
            temp = operatorsStack.pop(); 
            suffixExpr.push(temp);  
            numbersStack.push(calc(numbersStack, temp));  
        }  
          
        //printStack(suffixExpr);  
        // System.out.println("\ncalc result\t" + numbersStack.pop());  
        // return numbersStack.pop();
		return numbersStack.pop();
    }
	
	
	/**
	 * 判断连个op的优先级,op1优先级高于op2 return 1
	 * @param op
	 */
	
	private static int priorityCompare(char op1, char op2) {  
		switch (op1) {  
	    case '+':  
	    case '-':  
	        return (op2 == '*' || op2 == '/' ? -1 : 0);  
	    case '*':  
	    case '/':  
	        return (op2 == '+' || op2 == '-' ? 1 : 0);  
	    }  
	    return 1;  
	}  
	
    private static double calc(Stack<Double> numbersStack, char op) {  
        double num1 = numbersStack.pop();  
        double num2 = numbersStack.pop();  
        return calc(num2, num1, op);  
    }
    
    private static double calc(double num1, double num2, char op) throws IllegalArgumentException {  
        switch (op) {  
        case '+':  
            return num1 + num2;  
        case '-':  
            return num1 - num2;  
        case '*':  
            return num1 * num2;  
        case '/':  
            if (num2 == 0)  
                throw new IllegalArgumentException("divisor can't be 0.");  
            return num1 / num2;  
        default:  
            return 0;  
        }  
    }  
     
    private static int getEndPosOfDigit(String input, int start) {  
        char c;  
        int end = start + 1;  
        for (int i = start + 1; i < input.length(); i++) {  
            c = input.charAt(i);
            if (Character.isDigit(c) || c == '.') {
            	// 修复表达式中只有一个数字没有操作符的情况
            	end = i + 1;
                continue;  
            } else {  
                end = i;  
                break;  
            } 
            // 训话结束
        }  
        return end; 
    }  
        
	
	/**
	 * 判断该字符是否是操作符
	 * @param 操作符
	 */
	private static boolean isOperator(char c) {
		return (c == '+' || c == '-' || c == '*' || c== '/');
	}
}
