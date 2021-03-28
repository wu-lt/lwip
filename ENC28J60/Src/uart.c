#include "uart.h"
/****************************************************************************
* 名    称：void USART_OUT(USART_TypeDef* USARTx, uint8_t *Data,...)
* 功    能：格式化串口输出函数
* 入口参数：USARTx:  指定串口
			Data：   发送数组
			...:     不定参数
* 出口参数：无
* 说    明：格式化串口输出函数
        	"\r"	回车符	   USART_OUT(USART1, "abcdefg\r")
			"\n"	换行符	   USART_OUT(USART1, "abcdefg\r\n")
			"%s"	字符串	   USART_OUT(USART1, "字符串是：%s","abcdefg")
			"%d"	十进制	   USART_OUT(USART1, "a=%d",10)
* 调用方法：无
****************************************************************************/
void USART_OUT(USART_TypeDef *USARTx, uint8_t *Data, ...)
{
	const char *s;
	int d;
	char buf[16];
	va_list ap;
	va_start(ap, Data);

	while(*Data != 0) 				                        //判断是否到达字符串结束符
	{
		if(*Data == 0x5c) 									 //'\'
		{
			switch (*++Data)
			{
			case 'r':							          //回车符
				USART_SendData(USARTx, 0x0d);

				Data++;
				break;
			case 'n':							          //换行符
				USART_SendData(USARTx, 0x0a);
				Data++;
				break;

			default:
				Data++;
				break;
			}
		}
		else if(*Data == '%') 									 //
		{
			switch (*++Data)
			{
			case 's':										  //字符串
				s = va_arg(ap, const char *);
				for ( ; *s; s++)
				{
					USART_SendData(USARTx, *s);
					while(USART_GetFlagStatus(USARTx, USART_FLAG_TC) == RESET);
				}
				Data++;
				break;
			case 'd':										  //十进制
				d = va_arg(ap, int);
				itoa(d, buf, 10);
				for (s = buf; *s; s++)
				{
					USART_SendData(USARTx, *s);
					while(USART_GetFlagStatus(USARTx, USART_FLAG_TC) == RESET);
				}
				Data++;
				break;
			default:
				Data++;
				break;
			}
		}
		else USART_SendData(USARTx, *Data++);
		while(USART_GetFlagStatus(USARTx, USART_FLAG_TC) == RESET);
	}
}

/******************************************************
		整形数据转字符串函数
        char *itoa(int value, char *string, int radix)
		radix=10 标示是10进制	非十进制，转换结果为0;

	    例：d=-379;
		执行	itoa(d, buf, 10); 后

		buf="-379"
**********************************************************/
char *itoa(int value, char *string, int radix)
{
	int     i, d;
	int     flag = 0;
	char    *ptr = string;

	/* This implementation only works for decimal numbers. */
	if (radix != 10)
	{
		*ptr = 0;
		return string;
	}

	if (!value)
	{
		*ptr++ = 0x30;
		*ptr = 0;
		return string;
	}

	/* if this is a negative value insert the minus sign. */
	if (value < 0)
	{
		*ptr++ = '-';

		/* Make the value positive. */
		value *= -1;
	}

	for (i = 10000; i > 0; i /= 10)
	{
		d = value / i;

		if (d || flag)
		{
			*ptr++ = (char)(d + 0x30);
			value -= (d * i);
			flag = 1;
		}
	}

	/* Null terminate the string. */
	*ptr = 0;

	return string;

} /* NCL_Itoa */


/****************************************************************************
* 名    称：void Usart1_Init(void)
* 功    能：串口1初始化函数
* 入口参数：无
* 出口参数：无
* 说    明：
* 调用方法：无
****************************************************************************/
void Usart1_Init(void)
{
	GPIO_InitTypeDef GPIO_InitStructure;
	USART_InitTypeDef USART_InitStructure;

	RCC_APB2PeriphClockCmd( RCC_APB2Periph_USART1 , ENABLE);	 		//使能串口1时钟

	GPIO_InitStructure.GPIO_Pin = GPIO_Pin_9;	         		 		//USART1 TX
	GPIO_InitStructure.GPIO_Speed = GPIO_Speed_50MHz;
	GPIO_InitStructure.GPIO_Mode = GPIO_Mode_AF_PP;    		 		//复用推挽输出
	GPIO_Init(GPIOA, &GPIO_InitStructure);		    		 		//A端口

	GPIO_InitStructure.GPIO_Pin = GPIO_Pin_10;	         	 		//USART1 RX
	GPIO_InitStructure.GPIO_Mode = GPIO_Mode_IN_FLOATING;   	 		//复用开漏输入
	GPIO_Init(GPIOA, &GPIO_InitStructure);		         	 		//A端口

	USART_InitStructure.USART_BaudRate = 115200;						//速率115200bps
	USART_InitStructure.USART_WordLength = USART_WordLength_8b;		//数据位8位
	USART_InitStructure.USART_StopBits = USART_StopBits_1;			//停止位1位
	USART_InitStructure.USART_Parity = USART_Parity_No;				//无校验位
	USART_InitStructure.USART_HardwareFlowControl = USART_HardwareFlowControl_None;   //无硬件流控
	USART_InitStructure.USART_Mode = USART_Mode_Rx | USART_Mode_Tx;					//收发模式

	/* Configure USART1 */
	USART_Init(USART1, &USART_InitStructure);							//配置串口参数函数
	/* Enable the USART1 */
	USART_Cmd(USART1, ENABLE);
}

/******************* (C) COPYRIGHT 2018 Pang *****END OF FILE****/
