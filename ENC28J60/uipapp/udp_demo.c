#include "stm32f10x.h"
#include "uip.h"  
#include "enc28j60.h"
#include "uipopt.h"
#include "uip_arch.h"
#include <string.h>	 
extern void USART_OUT(USART_TypeDef* USARTx, uint8_t *Data,...);

#define LED1_ON()	GPIO_SetBits(GPIOB,  GPIO_Pin_5);
#define LED2_ON()	GPIO_SetBits(GPIOD,  GPIO_Pin_6);
#define LED3_ON()	GPIO_SetBits(GPIOD,  GPIO_Pin_3);

#define LED1_OFF()	GPIO_ResetBits(GPIOB,  GPIO_Pin_5);
#define LED2_OFF()	GPIO_ResetBits(GPIOD,  GPIO_Pin_6);
#define LED3_OFF()	GPIO_ResetBits(GPIOD,  GPIO_Pin_3);

extern unsigned short LPORT;
extern uint8_t Vsync;

extern void tcp_demo_appcall(void);
void myudp_appcall(void);


/*******************************************************************************
*	函数名: void myudp_send(char *str,short n) 
*	参  数: 
*	返  回: 无
*	功  能: UDP 数据包发送
**************************************************************************/
void myudp_send(char *str,short n) 
{
   char   *nptr;   
   nptr = (char *)uip_appdata;       
   memcpy(nptr, str, n); 
   uip_udp_send(n);   				//发送n个数据 
}


/*******************************************************************************
*	函数名: void UDP_newdata(void) 
*	参  数: 
*	返  回: 无
*	功  能: UDP 数据包发送
**************************************************************************/
void UDP_newdata(void) 
{ 
  
} 
/*******************************************************************************
*	函数名: void myudp_appcall(void) ) 
*	参  数: 
*	返  回: 无
*	功  能: UDP主函数
**************************************************************************/
void myudp_appcall(void) 
{
   if(uip_newdata()) 						
   { 
       UDP_newdata();      
   } 
} 

/******************* (C) COPYRIGHT 2018 Pang *****END OF FILE****/


