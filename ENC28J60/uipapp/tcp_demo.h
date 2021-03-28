
#ifndef __TCP_DEMO_H__
#define __TCP_DEMO_H__

/* Since this file will be included by uip.h, we cannot include uip.h
   here. But we might need to include uipopt.h if we need the u8_t and
   u16_t datatypes. */
#include "uipopt.h"

#include "psock.h"

/* ͨ�ų���״̬��(�û������Լ�����) */
enum
{
	STATE_CMD		= 0,	/* �������״̬ */
	STATE_TX_TEST	= 1,	/* �����������ݰ�״̬(�ٶȲ���) */
	STATE_RX_TEST	= 2		/* �����������ݰ�״̬(�ٶȲ���) */
};

/*
	���� uip_tcp_appstate_t �������ͣ��û��������Ӧ�ó�����Ҫ�õ�
	��Ա��������Ҫ���Ľṹ�����͵����֣���Ϊ����������ᱻuip���á�

	uip.h �ж���� 	struct uip_conn  �ṹ���������� uip_tcp_appstate_t
*/
struct tcp_demo_appstate
{
	u8_t state;
	u8_t *textptr;
	int textlen;
};

typedef struct tcp_demo_appstate uip_tcp_appstate_t;

/* ����Ӧ�ó���ص����� */
#ifndef UIP_APPCALL

#define UIP_APPCALL tcp_server_appcall
#define UIP_UDP_APPCALL myudp_appcall
#endif

void tcp_demo_init(void);

#endif
