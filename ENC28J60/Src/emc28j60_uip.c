#include "stm32f10x.h"
#include  "uip.h"
#include  "enc28j60.h"
#include "uipopt.h"
#include "uip_arch.h"
#include <string.h>

//���ڹ̶�IP��ַ���ش򿪺��IP���ã�������û�������
#define UIP_DRIPADDR0   192
#define UIP_DRIPADDR1   168
#define UIP_DRIPADDR2   1
#define UIP_DRIPADDR3   15

void etherdev_init(void);
extern void httpd_appcall(void);
extern void tcp_demo_appcall(void);
/*=============================================================================
  ϵͳȫ��ı���
  =============================================================================*/
#define ETH_ADDR_LEN			6

static unsigned char mymac[6] = {0x04, 0x02, 0x35, 0x00, 0x00, 0x01}; //MAC��ַ
/*******************************************************************************
*	������: etherdev_init
*	��  ��: ��
*	��  ��: ��
*	��  ��: uIP �ӿں���,��ʼ������
********************************************************************************/
void etherdev_init(void)
{
	u8 i;
	/*��ʼ�� enc28j60*/
	enc28j60Init(mymac);
	//��IP��ַ��MAC��ַд����ԵĻ�����	ipaddr[] macaddr[]
	//init_ip_arp_udp_tcp(mymac,myip,mywwwport);
	for (i = 0; i < 6; i++)
	{
		uip_ethaddr.addr[i] = mymac[i];
	}
	//ָʾ��״̬:0x476 is PHLCON LEDA(��)=links status, LEDB(��)=receive/transmit
	//enc28j60PhyWrite(PHLCON,0x7a4);
	//PHLCON��PHY ģ��LED ���ƼĴ���
	enc28j60PhyWrite(PHLCON, 0x0476);
	enc28j60clkout(2); // change clkout from 6.25MHz to 12.5MHz

}


/*******************************************************************************
*	������: void tcp_server_appcall(void)
*	��  ��:
*	��  ��: ��
*	��  ��: Ӧ�ýӿں���--���TCP��������HTTP������
**************************************************************************/
void tcp_server_appcall(void)
{
	switch(uip_conn->lport)
	{
	case HTONS(80):			           //http������
		httpd_appcall();
		break;
	case HTONS(1200):				   //TCP������
		tcp_demo_appcall();
		break;
	}
}

/******************* (C) COPYRIGHT 2011 �ܶ�STM32 *****END OF FILE****/

