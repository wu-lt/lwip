/****************************************************************************
*
* �ļ���: main.c
* ���ݼ���:
*		��������ֲuIP-1.0Э��ջ����ʾ�������PC���TCPͨ�š��Զ�����һ���򵥵�Ӧ�ò�
*	ͨ��Э�顣������ʵ�ֵĹ����У�
*		��1��ͨ��PC�����ư����ϵ�LED��
*		��2��ʵ����һ���򵥵�Web��������
*	   ѡ�õ�����оƬENC28J60��10M����
*		���������õ�ȱʡIP��ַ�� 192.168.1.15��Ĭ�ϵ�TCP�����������˿���1200, WEB�����������˿�80��
	    UDP�����������˿�2000��
*		�����幤����TCP������ģʽ��PC��������TCP�ͻ���ģʽ��
*	 	PC������Ҫ��������������������
*	�û����������²��ԣ�
*	��1��ping ���� (ICMP)
*		���windows ��ʼ-���У�ִ��cmd���Ȼ����dos�������� ping 192.168.1.15
*		Ӧ�ÿ������½����
*			Reply from 192.168.1.15: bytes=32 time<1ms TTL=128
*			Reply from 192.168.1.15: bytes=32 time<1ms TTL=128
*			Reply from 192.168.1.15: bytes=32 time<1ms TTL=128
*			Reply from 192.168.1.15: bytes=32 time<1ms TTL=128
*
*	��2��PC����TCP�ͻ��˿��ƿ������ϵ�LED����  (TCP������)
*		���������������������˿�ѡ��1200��������IP��ַѡ��192.168.1.15�� Э������ѡ��TCP�ͻ��ˣ�
    ������Ӱ�ť�����������봰���������LED�������ַ�����Ȼ�������ͣ��������϶�Ӧ��LED�ƻᷢ��
	�仯��
*		����������£� (ĩβ��0x00�ͻس��ַ�)
*		ledon 1     ----- ����LED1
*		ledoff 1    ----- �ر�LED1
*		ledon 2     ----- ����LED2
*		ledoff 2    ----- �ر�LED2
*		ledon 3     ----- ����LED3
*		ledoff 3    ----- �ر�LED3
*
    ��3��WEB��ҳ���ƿ������ϵ�LED����  (WEB������)
		 ��IE��������ڵ�ַ������http://192.168.1.15  ���Կ�������uIP��Web����ҳ��
	     �ڶԻ�������1-3,��ȷ����ť�����Կ�����Ӧ��LED������
		 ��ҳ������httpd-fsdata.c��data_index_html����� ��ͨ��amo�ı��С���߼���ת���ģ������ڷܶ���̳���ص���
	��4��UDP������ʵ��
		 ����UDP�˿�Ĭ����2000�� Զ��UDP����ֱ�����ӱ��ض˿ڣ�������ͨ��
		 ����������£� (ĩβ��0x00�ͻس��ַ�)
		 getname     ------���ذ��ӵ���Ϣ
*		 ledon 1     ----- ����LED1
*		 ledoff 1    ----- �ر�LED1
*		 ledon 2     ----- ����LED2
*		 ledoff 2    ----- �ر�LED2
*		 ledon 3     ----- ����LED3
*		 ledoff 3    ----- �ر�LED3
*
* �ļ���ʷ:
* �汾��  ����       ����    ˵��
* v0.2    2018-03-24 Pang  �������ļ�
* V0.3    2018-03-28 Pang  �����û�wy1635610��ָ�㣬������һЩӰ���ȶ���bug
          �漰���ļ�����uip.c  uip.h  http.c  httpd.h
*
*/

#include "stm32f10x_conf.h"
#include <stdio.h>
#include "systick.h"

#include <uip.h>
#include "uip_arp.h"
#include "tapdev.h"
#include "timer.h"
#include "enc28j60.h"
#include  <stdarg.h>

#include "fd_eth.h"
#include "uart.h"
#include "spi.h"



#define BUF ((struct uip_eth_hdr *)&uip_buf[0])
#define ETH_rec_f GPIO_ReadInputDataBit(GPIOA,GPIO_Pin_1)          //ENC28J60����֡����жϽ� 


void InitNet(void);
void UipPro(void);
void Delay(vu32 nCount);
void GPIO_Configuration(void);
void RCC_Configuration(void);
void USART_OUT(USART_TypeDef *USARTx, uint8_t *Data, ...);
char *itoa(int value, char *string, int radix);
void eth_poll(void);

unsigned short net_time05 = 0;
unsigned short net_time10 = 0;
unsigned char net_timeover05 = 0;
unsigned char net_timeover10 = 0;
unsigned char ETH_INT = 0;
void showStart(void);
//void showCamera();


extern void SPI1_Init(void);
#define RST   (1<<0)   				// PB14  
#define RST_SET(x) GPIOE->ODR=(GPIOE->ODR&~RST)|(x ? RST:0)



/****************************************************************************
* ��    �ƣ�void RCC_Configuration(void)
* ��    �ܣ�ϵͳʱ������Ϊ72MHZ�� ����ʱ������
* ��ڲ�������
* ���ڲ�������
* ˵    ����
* ���÷�������
****************************************************************************/
void RCC_Configuration(void)
{
	SystemInit();
	RCC_APB2PeriphClockCmd( RCC_APB2Periph_AFIO  , ENABLE);
	RCC_APB2PeriphClockCmd(RCC_APB2Periph_GPIOA | RCC_APB2Periph_GPIOB | RCC_APB2Periph_GPIOC
	                       | RCC_APB2Periph_GPIOD | RCC_APB2Periph_GPIOE , ENABLE);
}


void disableJTAG()
{
	/*����GPIOB������ʱ��*/
	RCC_APB2PeriphClockCmd(RCC_APB2Periph_AFIO | RCC_APB2Periph_GPIOB, ENABLE);
	//GPIO_PinRemapConfig(GPIO_Remap_SWJ_Disable,ENABLE);		//����������ΪJTAG�ڵ�GPIO��
	GPIO_PinRemapConfig(GPIO_Remap_SWJ_JTAGDisable, ENABLE);		//����PB����IO��JTAG����
}
/****************************************************************************
* ��    �ƣ�int main(void)
* ��    �ܣ��������
* ��ڲ�������
* ���ڲ�������
* ˵    ����
* ���÷�������
****************************************************************************/
int main(void)
{
	u8 u;
	RCC_Configuration();				 //ϵͳʱ������
	//NVIC_Configuration();
	GPIO_Configuration();                //IO�ڳ�ʼ��
	Usart1_Init();                       //����1��ʼ��


	/* ����systic��Ϊ1ms�ж� */
	if (SysTick_Config(72000))		     //ʱ�ӽ����ж�ʱ1msһ��  ���ڶ�ʱ
	{
		/* Capture error */
		while (1);
	}
  SPI1_Init();
	Delay(2);       					     //��ʱ10ms
	disableJTAG();



	InitNet();		/* ��ʼ�������豸�Լ�UIPЭ��ջ������IP��ַ */
	
	/* ����һ��TCP�����˿ں�http�����˿ڣ��˿ں�Ϊ1200��80 */
	uip_listen(HTONS(1200));
	uip_listen(HTONS(80));

	while (1)
	{
		//LED1_ON;
		UipPro();			  //�жϴ�����ȡ������ջ���
		eth_poll();           //��ʱ��ѯTCP��UDP�����շ�״̬	ARP����£� ����Ӧ

		USART_OUT(USART1, "SPI>>>>>>ok");
	}
}

void showStart()
{

}
/****************************************************************************
* ��    �ƣ�void eth_poll(void)
* ��    �ܣ���ʱ��ѯTCP�����շ�״̬	ARP����£� ����Ӧ
* ��ڲ�������
* ���ڲ�������
* ˵    ����
* ���÷�������
****************************************************************************/
void eth_poll(void)
{
	unsigned char i = 0;
	if(net_timeover05 == 1)			/* 0.5�붨ʱ����ʱ */
	{
		net_time05 = 0;
		net_timeover05 = 0;			/* ��λ0.5�붨ʱ�� */

		/* ��������ÿ��TCP����, UIP_CONNSȱʡ��10�� */
		for(i = 0; i < UIP_CONNS; i++)
		{
			uip_periodic(i);		/* ����TCPͨ���¼� */
			/*
				������ĺ���ִ�к������Ҫ�������ݣ���ȫ�ֱ��� uip_len > 0
				��Ҫ���͵�������uip_buf, ������uip_len  (����2��ȫ�ֱ���)
			*/
			if(uip_len > 0)
			{
				uip_arp_out();		//����̫��ͷ�ṹ������������ʱ����Ҫ����ARP����
				tapdev_send();		//�������ݵ���̫�����豸��������
			}
		}

#if UIP_UDP
		/* ��������ÿ��UDP����, UIP_UDP_CONNSȱʡ��10�� */
		for(i = 0; i < UIP_UDP_CONNS; i++)
		{
			uip_udp_periodic(i);	/*����UDPͨ���¼� */
			/* �������ĺ������õ�������Ӧ�ñ����ͳ�ȥ��ȫ�ֱ���uip_len�趨ֵ> 0 */
			if(uip_len > 0)
			{
				uip_arp_out();		//����̫��ͷ�ṹ������������ʱ����Ҫ����ARP����
				tapdev_send();		//�������ݵ���̫�����豸��������
			}
		}
#endif /* UIP_UDP */

		/* ÿ��10�����1��ARP��ʱ������ ���ڶ���ARP����,ARP��10�����һ�Σ��ɵ���Ŀ�ᱻ����*/
		if (net_timeover10 == 1)
		{
			net_time10 = 0;
			net_timeover10 = 0;		/* ��λ10�붨ʱ�� */
			uip_arp_timer();
		}
	}
}
/*******************************************************************************
*	��������UipPro
*	��  ��:
*	��  ��:
*	����˵�����жϴ�����ȡ������ջ���
********************************************************************/
void UipPro(void)
{
	if(ETH_INT == 1) 					//��������յ�����ʱ��������ж�
	{
rep:
		;
		ETH_INT = 0;
		uip_len = tapdev_read();	//�������豸��ȡһ��IP��,�������ݳ���
		if(uip_len > 0)			    //�յ�����
		{
			/* ����IP���ݰ�(ֻ��У��ͨ����IP���Żᱻ����) */
			if(BUF->type == htons(UIP_ETHTYPE_IP))   //��IP����
			{
				uip_arp_ipin();		   //ȥ����̫��ͷ�ṹ������ARP��
				uip_input();		   //IP������
				/*
					������ĺ���ִ�к������Ҫ�������ݣ���ȫ�ֱ��� uip_len > 0
					��Ҫ���͵�������uip_buf, ������uip_len  (����2��ȫ�ֱ���)
				*/
				if (uip_len > 0)		//�д����Ӧ����
				{
					uip_arp_out();		//����̫��ͷ�ṹ������������ʱ����Ҫ����ARP����
					tapdev_send();		//�������ݵ���̫�����豸��������
				}
			}
			/* ����arp���� */
			else if (BUF->type == htons(UIP_ETHTYPE_ARP))	//��ARP�����
			{
				uip_arp_arpin();		//������ARP��Ӧ������ARP����������󣬹����Ӧ���ݰ�
				/*
					������ĺ���ִ�к������Ҫ�������ݣ���ȫ�ֱ��� uip_len > 0
					��Ҫ���͵�������uip_buf, ������uip_len  (����2��ȫ�ֱ���)
				*/
				if (uip_len > 0)		//��ARP����Ҫ���ͻ�Ӧ
				{
					tapdev_send();		//��ARP��Ӧ����̫����
				}
			}
		}
	}
	else 	                   //��ֹ�����ɽ�������,��û�в����жϣ���ENC28J60�ж��ź�ʼ��Ϊ��˵����������
	{
		if(ETH_rec_f == 0) goto rep;
	}
}
/*******************************************************************************
*	��������InitNet
*	��  ��:
*	��  ��:
*	����˵������ʼ������Ӳ����UIPЭ��ջ�����ñ���IP��ַ
************************************************************/
void InitNet(void)
{
	uip_ipaddr_t ipaddr;
	tapdev_init();                     		 //ENC28J60��ʼ��

	USART_OUT(USART1, "uip_init\n\r");
	uip_init();								 //UIPЭ��ջ��ʼ��

	USART_OUT(USART1, "uip ip address : 192,168,1,15\r\n");
	uip_ipaddr(ipaddr, 192, 168, 1, 15);		 //����IP��ַ
	uip_sethostaddr(ipaddr);

	USART_OUT(USART1, "uip route address : 192,168,1,1\r\n");
	uip_ipaddr(ipaddr, 192, 168, 1, 1);		 //����Ĭ��·����IP��ַ
	uip_setdraddr(ipaddr);

	USART_OUT(USART1, "uip net mask : 255,255,255,0\r\n");
	uip_ipaddr(ipaddr, 255, 255, 255, 0);		 //������������
	uip_setnetmask(ipaddr);

}
/****************************************************************************
* ��    �ƣ�void GPIO_Configuration(void)
* ��    �ܣ�ͨ��IO������
* ��ڲ�������
* ���ڲ�������
* ˵    ����
* ���÷�����
****************************************************************************/
void GPIO_Configuration(void)
{

	GPIO_InitTypeDef GPIO_InitStructure;
	RCC_APB2PeriphClockCmd( RCC_APB2Periph_USART1 | RCC_APB2Periph_GPIOA | RCC_APB2Periph_GPIOB |
	                        RCC_APB2Periph_GPIOC | RCC_APB2Periph_GPIOD |
	                        RCC_APB2Periph_GPIOE, ENABLE);

	GPIO_InitStructure.GPIO_Pin = GPIO_Pin_1;	         	 	//ENC28J60��������ж�����
	GPIO_InitStructure.GPIO_Mode = GPIO_Mode_IPU;   	 		//�ڲ���������
	GPIO_Init(GPIOA, &GPIO_InitStructure);
}


void Delay(vu32 nCount)
{
	for(; nCount != 0; nCount--);
}

