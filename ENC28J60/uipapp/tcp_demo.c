#include "tcp_demo.h"
#include "uip.h"
#include <string.h>
#include <stdio.h>
#include "stm32f10x.h"

static void aborted(void);
static void timedout(void);
static void closed(void);
static void connected(void);
static void newdata(void);
static void acked(void);
static void senddata(void);

static uint8_t test_data[2048];   /* 1K�������� */

#define LED1_ON()	GPIO_SetBits(GPIOB,  GPIO_Pin_5);
#define LED2_ON()	GPIO_SetBits(GPIOD,  GPIO_Pin_6);
#define LED3_ON()	GPIO_SetBits(GPIOD,  GPIO_Pin_3);

#define LED1_OFF()	GPIO_ResetBits(GPIOB,  GPIO_Pin_5);
#define LED2_OFF()	GPIO_ResetBits(GPIOD,  GPIO_Pin_6);
#define LED3_OFF()	GPIO_ResetBits(GPIOD,  GPIO_Pin_3);

extern void USART_OUT(USART_TypeDef* USARTx, uint8_t *Data,...);

/*******************************************************************************
*	������: tcp_demo_appcall
*	��  ��: ��
*	��  ��: ��
*	����˵��������һ���ص���������h�ļ��У����Ƕ��� UIP_APPCALL�ĺ���� tcp_demo_appcall
*		��uip�¼�����ʱ��UIP_APPCALL �����ᱻ���á�
*		���� : ��һ��TCP���ӱ�����ʱ�����µ����ݵ�������Ѿ���Ӧ��������Ҫ�ط����¼�
*
***************************************************************************/
void tcp_demo_appcall(void)
{
	if (uip_aborted())
	{
		USART_OUT(USART1,"uip_aborted!\r\n");
		aborted();
	}

	if (uip_timedout())
	{
		USART_OUT(USART1,"uip_timedout!\r\n");
		timedout();
	}

	if (uip_closed())
	{
		USART_OUT(USART1,"uip_closed!\r\n");
		closed();
	}

	if (uip_connected())
	{
		USART_OUT(USART1,"uip_connected!\r\n");
		connected();
	}

	if (uip_acked())
	{
		acked();
	}

	/* ���յ�һ���µ�TCP���ݰ���׼����Ҫ�������� */
	if (uip_newdata())
	{
		newdata();
	}

	/* ����Ҫ�ط��������ݵ�����ݰ��ʹ���ӽ���ʱ��֪ͨuip�������� */
	if (uip_rexmit() ||	uip_newdata() || uip_acked() ||	uip_connected() || uip_poll())
	{
		senddata();
	}
}

/*******************************************************************************
*	������: aborted
*	��  ��: ��
*	��  ��: ��
*	����˵������TCP�����쳣��ֹʱ�����ô˺�����
***************************************************************************/
static void aborted(void)
{
	;
}

static void timedout(void)
{
	;
}

static void closed(void)
{
	;
}

/*******************************************************************************
*	������: connected
*	��  ��: ��
*	��  ��: ��
*	����˵������TCP���ӽ���ʱ�����ô˺�����
***************************************************************************/
static void connected(void)
{  
	/*
		uip_conn�ṹ����һ��"appstate"�ֶ�ָ��Ӧ�ó����Զ���Ľṹ�塣
		����һ��sָ�룬��Ϊ�˱���ʹ�á�

		����Ҫ�ٵ���Ϊÿ��uip_conn�����ڴ棬����Ѿ���uip�з�����ˡ�
		��uip.c �� ����ش������£�
			struct uip_conn *uip_conn;
			struct uip_conn uip_conns[UIP_CONNS]; //UIP_CONNSȱʡ=10
		������1�����ӵ����飬֧��ͬʱ�����������ӡ�
		uip_conn��һ��ȫ�ֵ�ָ�룬ָ��ǰ��tcp��udp���ӡ�
	*/
	struct tcp_demo_appstate *s = (struct tcp_demo_appstate *)&uip_conn->appstate;

	memset(test_data, 0x55, 2048);

	s->state = STATE_CMD;						        //ָ��״̬
	s->textlen = 0;

	s->textptr = "Connect STM32-FD Board Success!";
	s->textlen = strlen((char *)s->textptr);
}

/*******************************************************************************
*	������: TCPCmd
*	��  ��: ��
*	��  ��: ��
*	����˵��������PC������������͵������������Ӧ�Ĵ���
***************************************************************************/
void TCP_Cmd(struct tcp_demo_appstate *s)
{
	uint8_t led;
	
	/* ����LED
		�﷨��ledon n  (n : 1 - 3) 
		���� �� 
		ledon 2 ��ʾ����LED2	
	*/
	if ((uip_len == 7) && (memcmp("ledon ", uip_appdata, 6) == 0))
	{
		led = ((uint8_t *)uip_appdata)[6]; /* ������LED��� */
		if (led == '1')
		{
			LED1_ON();
			s->textptr = "Led 1 On!";
		}
		else if (led == '2')
		{
			LED2_ON();
			s->textptr = "Led 2 On!";			
		}
		else if (led == '3')
		{
			LED3_ON();
			s->textptr = "Led 3 On!";
		}
	
		s->textlen = strlen((char *)s->textptr);
	}
	/* �ر�LED
		�﷨��ledoff n  (n : 1 - 4) 
		���� �� 
		ledon 2 ��ʾ����LED2	
	*/
	else if ((uip_len == 8) && (memcmp("ledoff ", uip_appdata, 7) == 0))
	{
		led = ((uint8_t *)uip_appdata)[7]; /* ������LED��� */
		if (led == '1')
		{
			LED1_OFF();
			s->textptr = "Led 1 off!";
		}
		else if (led == '2')
		{
			LED2_OFF();
			s->textptr = "Led 2 Off!";				
		}
		else if (led == '3')
		{
			LED3_OFF();
			s->textptr = "Led 3 Off!";
		}
		
		s->textlen = strlen((char *)s->textptr);
	}
	/* �������ݲ��� txtest
		�﷨��txtest
		���� �� 
		
	*/
	else if ((uip_len == 6) && (memcmp("txtest", uip_appdata, 6) == 0))
	{
		s->state = STATE_TX_TEST;

		s->textptr = test_data;
		s->textlen = 1400;
	}
	/* �������ݲ��� rxtest
		�﷨��rxtest
		���� �� 
		
	*/
	else if ((uip_len == 6) && (memcmp("rxtest", uip_appdata, 6) == 0))
	{
		s->state = STATE_RX_TEST;
		s->textptr = "Ok";
		s->textlen = 2;
	}
	else
	{
		s->textptr = "Unknow Command!\r\n";
		s->textlen = strlen((char *)s->textptr);
	}
}

/*******************************************************************************
*	������: newdata
*	��  ��: ��
*	��  ��: ��
*	����˵�������յ��µ�TCP��ʱ�����ô˺�����׼�����ݣ�������ʱ�����͡�
***************************************************************************/
static void newdata(void)
{
	struct tcp_demo_appstate *s = (struct tcp_demo_appstate *)&uip_conn->appstate;
	
	if (s->state == STATE_CMD)
	{
		USART_OUT(USART1,"uip_newdata!\r\n");
		TCP_Cmd(s);
	}
	else if (s->state == STATE_TX_TEST)	/* �ϴ�����״̬ */
	{
		/* �ڷ��Ͳ���״̬������յ�PC�����͵��������ݣ����˳�����״̬ */
		if ((uip_len == 1) && (((uint8_t *)uip_appdata)[0] == 'A'))
		{
			;/* �������� */
		}
		else
		{
			/* �˵�����״̬ */
	   		s->state = STATE_CMD;
			s->textlen = 0;
		}
	}
	else if (s->state == STATE_RX_TEST)	/* �´�����״̬ */
	{				
		if ((uip_len == 4) && (memcmp("stop", uip_appdata, 4) == 0))
		{
			/* �˵�����״̬ */
	   		s->state = STATE_CMD;
			s->textlen = 0;
		}
		else
		{ 			
			s->textptr = uip_appdata;   	/* ��ͻ��˷����յ������� */
			s->textlen = uip_len;
		}
	}
}

/*******************************************************************************
*	������: acked
*	��  ��: ��
*	��  ��: ��
*	����˵���������͵�TCP���ɹ��ʹ�ʱ�����ô˺�����
***************************************************************************/
static void acked(void)
{
	struct tcp_demo_appstate *s = (struct tcp_demo_appstate *)&uip_conn->appstate;

	switch(s->state)
	{
		case STATE_CMD:		 /* ������״̬ */
			s->textlen = 0;

			/* 
				ֻ������״̬��ӡ������Ϣ 
				���ⷢ�Ͳ���ʱ��Ӱ��ͨ���ٶ�		
			*/
			USART_OUT(USART1,"uip_acked!\r\n");
			break;

		case STATE_TX_TEST:
			s->textptr = test_data;	/* �������� */
			s->textlen = 1400;
			break;

		case STATE_RX_TEST:
			s->textlen = 0;
			break;
	}
}

/*******************************************************************************
*	������: senddata
*	��  ��: ��
*	��  ��: ��
*	����˵��������tcp���ݡ�
***************************************************************************/
static void senddata(void)
{
	struct tcp_demo_appstate *s = (struct tcp_demo_appstate *)&uip_conn->appstate;

	if (s->textlen > 0)
	{
		/*
			��������������緢��TCP���ݰ�,
				s->textptr : ���͵����ݰ�������ָ��
				s->textlen �����ݰ��Ĵ�С����λ�ֽڣ�
		*/
		uip_send(s->textptr, s->textlen);
	}
}

/*******************************************************************************
*	������: uip_log
*	��  ��: m: �ַ���
*	��  ��: ��
*	����˵����uIP�ĵ��Դ�ӡ��䡣
*		����ͨ��ע�� #define UIP_CONF_LOGGING     ���رյ������
***************************************************************************/
void uip_log(char *m)
{
	USART_OUT(USART1,"uIP log message: %s\r\n", m);
}

/******************* (C) COPYRIGHT 2011 �ܶ�STM32 *****END OF FILE****/
