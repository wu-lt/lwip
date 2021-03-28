#include "stm32f10x_conf.h"
#include <stdio.h>	 
#include "systick.h"
#include  <stdarg.h>
void USART_OUT(USART_TypeDef* USARTx, uint8_t *Data,...);
char *itoa(int value, char *string, int radix);
void Usart1_Init(void);

