/****************************************************************************
* Copyright (C), 2011 �ܶ�Ƕ��ʽ������ www.ourstm.net
*	           
* QQ: 9191274, ������sun68, Email: sun68@163.com 
* �Ա����̣�ourstm.taobao.com  
*
* �ļ���: uip_conf.h
* ���ݼ���:
*       uip����
*
* �ļ���ʷ:
* �汾��  ����       ����    ˵��
* v0.1    2011-8-23 sun68  �޸ĸ��ļ�
*
*/

#ifndef __UIP_CONF_H__
#define __UIP_CONF_H__

#include <inttypes.h>


typedef uint8_t u8_t;

typedef uint16_t u16_t;	

typedef unsigned short uip_stats_t;


/* ���TCP������ */
#define UIP_CONF_MAX_CONNECTIONS 10

/* ���˿ڼ����� */
#define UIP_CONF_MAX_LISTENPORTS 10	  

/* uIP �����С*/
#define UIP_CONF_BUFFER_SIZE     4096

/* CPU�ֽ�˳�� */ 
#define UIP_CONF_BYTE_ORDER  UIP_LITTLE_ENDIAN

/* ��־����	 */			
#define UIP_CONF_LOGGING         1

/* UDP֧�ֿ���*/
#define UIP_CONF_UDP             1

/*UDPУ��Ϳ��� */
#define UIP_CONF_UDP_CHECKSUMS   1

/* uIPͳ�ƿ��� */
#define UIP_CONF_STATISTICS      1


#include "tcp_demo.h"
#endif
