/****************************************************************************
*
* 文件名: main.c
* 内容简述:
*		本例程移植uIP-1.0协议栈，演示开发板和PC间的TCP通信。自定义了一个简单的应用层
*	通信协议。本例程实现的功能有：
*		（1）通过PC机控制板子上的LED；
*		（2）实现了一个简单的Web服务器。
*	   选用的网卡芯片ENC28J60，10M带宽。
*		本例程设置的缺省IP地址是 192.168.1.15，默认的TCP服务器监听端口是1200, WEB服务器监听端口80，
	    UDP服务器监听端口2000。
*		开发板工作在TCP服务器模式。PC机工作在TCP客户端模式。
*	 	PC机上需要运行网络调试助手软件。
*	用户可以做如下测试：
*	（1）ping 试验 (ICMP)
*		点击windows 开始-运行，执行cmd命令，然后在dos窗口输入 ping 192.168.1.15
*		应该看到如下结果：
*			Reply from 192.168.1.15: bytes=32 time<1ms TTL=128
*			Reply from 192.168.1.15: bytes=32 time<1ms TTL=128
*			Reply from 192.168.1.15: bytes=32 time<1ms TTL=128
*			Reply from 192.168.1.15: bytes=32 time<1ms TTL=128
*
*	（2）PC机的TCP客户端控制开发板上的LED试验  (TCP服务器)
*		运行网络调试助手软件，端口选择1200，服务器IP地址选择192.168.1.15， 协议类型选择TCP客户端，
    点击连接按钮，在命令输入窗口输入控制LED的命令字符串，然后点击发送，开发板上对应的LED灯会发生
	变化。
*		命令代码如下： (末尾无0x00和回车字符)
*		ledon 1     ----- 点亮LED1
*		ledoff 1    ----- 关闭LED1
*		ledon 2     ----- 点亮LED2
*		ledoff 2    ----- 关闭LED2
*		ledon 3     ----- 点亮LED3
*		ledoff 3    ----- 关闭LED3
*
    （3）WEB网页控制开发板上的LED试验  (WEB服务器)
		 打开IE浏览器，在地址栏输入http://192.168.1.15  可以看到基于uIP的Web测试页面
	     在对话框输入1-3,点确定按钮，可以控制相应的LED点亮。
		 网页保存在httpd-fsdata.c的data_index_html数组里， 是通过amo的编程小工具集合转换的，可以在奋斗论坛下载到。
	（4）UDP服务器实验
		 本地UDP端口默认是2000， 远端UDP可以直接连接本地端口，并进行通信
		 命令代码如下： (末尾无0x00和回车字符)
		 getname     ------返回板子的信息
*		 ledon 1     ----- 点亮LED1
*		 ledoff 1    ----- 关闭LED1
*		 ledon 2     ----- 点亮LED2
*		 ledoff 2    ----- 关闭LED2
*		 ledon 3     ----- 点亮LED3
*		 ledoff 3    ----- 关闭LED3
*
* 文件历史:
* 版本号  日期       作者    说明
* v0.2    2018-03-24 Pang  创建该文件
* V0.3    2018-03-28 Pang  根据用户wy1635610的指点，修正了一些影响稳定的bug
          涉及的文件包括uip.c  uip.h  http.c  httpd.h
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
#define ETH_rec_f GPIO_ReadInputDataBit(GPIOA,GPIO_Pin_1)          //ENC28J60接收帧完成中断脚 


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
* 名    称：void RCC_Configuration(void)
* 功    能：系统时钟配置为72MHZ， 外设时钟配置
* 入口参数：无
* 出口参数：无
* 说    明：
* 调用方法：无
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
	/*开启GPIOB的外设时钟*/
	RCC_APB2PeriphClockCmd(RCC_APB2Periph_AFIO | RCC_APB2Periph_GPIOB, ENABLE);
	//GPIO_PinRemapConfig(GPIO_Remap_SWJ_Disable,ENABLE);		//屏蔽所有作为JTAG口的GPIO口
	GPIO_PinRemapConfig(GPIO_Remap_SWJ_JTAGDisable, ENABLE);		//屏蔽PB口上IO口JTAG功能
}
/****************************************************************************
* 名    称：int main(void)
* 功    能：程序入口
* 入口参数：无
* 出口参数：无
* 说    明：
* 调用方法：无
****************************************************************************/
int main(void)
{
	u8 u;
	RCC_Configuration();				 //系统时钟设置
	//NVIC_Configuration();
	GPIO_Configuration();                //IO口初始化
	Usart1_Init();                       //串口1初始化


	/* 配置systic作为1ms中断 */
	if (SysTick_Config(72000))		     //时钟节拍中断时1ms一次  用于定时
	{
		/* Capture error */
		while (1);
	}
  SPI1_Init();
	Delay(2);       					     //延时10ms
	disableJTAG();



	InitNet();		/* 初始化网络设备以及UIP协议栈，配置IP地址 */
	
	/* 创建一个TCP监听端口和http监听端口，端口号为1200，80 */
	uip_listen(HTONS(1200));
	uip_listen(HTONS(80));

	while (1)
	{
		//LED1_ON;
		UipPro();			  //中断触发读取网络接收缓存
		eth_poll();           //定时查询TCP及UDP连接收发状态	ARP表更新， 并响应

		USART_OUT(USART1, "SPI>>>>>>ok");
	}
}

void showStart()
{

}
/****************************************************************************
* 名    称：void eth_poll(void)
* 功    能：定时查询TCP连接收发状态	ARP表更新， 并响应
* 入口参数：无
* 出口参数：无
* 说    明：
* 调用方法：无
****************************************************************************/
void eth_poll(void)
{
	unsigned char i = 0;
	if(net_timeover05 == 1)			/* 0.5秒定时器超时 */
	{
		net_time05 = 0;
		net_timeover05 = 0;			/* 复位0.5秒定时器 */

		/* 轮流处理每个TCP连接, UIP_CONNS缺省是10个 */
		for(i = 0; i < UIP_CONNS; i++)
		{
			uip_periodic(i);		/* 处理TCP通信事件 */
			/*
				当上面的函数执行后，如果需要发送数据，则全局变量 uip_len > 0
				需要发送的数据在uip_buf, 长度是uip_len  (这是2个全局变量)
			*/
			if(uip_len > 0)
			{
				uip_arp_out();		//加以太网头结构，在主动连接时可能要构造ARP请求
				tapdev_send();		//发送数据到以太网（设备驱动程序）
			}
		}

#if UIP_UDP
		/* 轮流处理每个UDP连接, UIP_UDP_CONNS缺省是10个 */
		for(i = 0; i < UIP_UDP_CONNS; i++)
		{
			uip_udp_periodic(i);	/*处理UDP通信事件 */
			/* 如果上面的函数调用导致数据应该被发送出去，全局变量uip_len设定值> 0 */
			if(uip_len > 0)
			{
				uip_arp_out();		//加以太网头结构，在主动连接时可能要构造ARP请求
				tapdev_send();		//发送数据到以太网（设备驱动程序）
			}
		}
#endif /* UIP_UDP */

		/* 每隔10秒调用1次ARP定时器函数 用于定期ARP处理,ARP表10秒更新一次，旧的条目会被抛弃*/
		if (net_timeover10 == 1)
		{
			net_time10 = 0;
			net_timeover10 = 0;		/* 复位10秒定时器 */
			uip_arp_timer();
		}
	}
}
/*******************************************************************************
*	函数名：UipPro
*	输  入:
*	输  出:
*	功能说明：中断触发读取网络接收缓存
********************************************************************/
void UipPro(void)
{
	if(ETH_INT == 1) 					//当网络接收到数据时，会产生中断
	{
rep:
		;
		ETH_INT = 0;
		uip_len = tapdev_read();	//从网络设备读取一个IP包,返回数据长度
		if(uip_len > 0)			    //收到数据
		{
			/* 处理IP数据包(只有校验通过的IP包才会被接收) */
			if(BUF->type == htons(UIP_ETHTYPE_IP))   //是IP包吗？
			{
				uip_arp_ipin();		   //去除以太网头结构，更新ARP表
				uip_input();		   //IP包处理
				/*
					当上面的函数执行后，如果需要发送数据，则全局变量 uip_len > 0
					需要发送的数据在uip_buf, 长度是uip_len  (这是2个全局变量)
				*/
				if (uip_len > 0)		//有带外回应数据
				{
					uip_arp_out();		//加以太网头结构，在主动连接时可能要构造ARP请求
					tapdev_send();		//发送数据到以太网（设备驱动程序）
				}
			}
			/* 处理arp报文 */
			else if (BUF->type == htons(UIP_ETHTYPE_ARP))	//是ARP请求包
			{
				uip_arp_arpin();		//如是是ARP回应，更新ARP表；如果是请求，构造回应数据包
				/*
					当上面的函数执行后，如果需要发送数据，则全局变量 uip_len > 0
					需要发送的数据在uip_buf, 长度是uip_len  (这是2个全局变量)
				*/
				if (uip_len > 0)		//是ARP请求，要发送回应
				{
					tapdev_send();		//发ARP回应到以太网上
				}
			}
		}
	}
	else 	                   //防止大包造成接收死机,当没有产生中断，而ENC28J60中断信号始终为低说明接收死机
	{
		if(ETH_rec_f == 0) goto rep;
	}
}
/*******************************************************************************
*	函数名：InitNet
*	输  入:
*	输  出:
*	功能说明：初始化网络硬件、UIP协议栈、配置本机IP地址
************************************************************/
void InitNet(void)
{
	uip_ipaddr_t ipaddr;
	tapdev_init();                     		 //ENC28J60初始化

	USART_OUT(USART1, "uip_init\n\r");
	uip_init();								 //UIP协议栈初始化

	USART_OUT(USART1, "uip ip address : 192,168,1,15\r\n");
	uip_ipaddr(ipaddr, 192, 168, 1, 15);		 //设置IP地址
	uip_sethostaddr(ipaddr);

	USART_OUT(USART1, "uip route address : 192,168,1,1\r\n");
	uip_ipaddr(ipaddr, 192, 168, 1, 1);		 //设置默认路由器IP地址
	uip_setdraddr(ipaddr);

	USART_OUT(USART1, "uip net mask : 255,255,255,0\r\n");
	uip_ipaddr(ipaddr, 255, 255, 255, 0);		 //设置网络掩码
	uip_setnetmask(ipaddr);

}
/****************************************************************************
* 名    称：void GPIO_Configuration(void)
* 功    能：通用IO口配置
* 入口参数：无
* 出口参数：无
* 说    明：
* 调用方法：
****************************************************************************/
void GPIO_Configuration(void)
{

	GPIO_InitTypeDef GPIO_InitStructure;
	RCC_APB2PeriphClockCmd( RCC_APB2Periph_USART1 | RCC_APB2Periph_GPIOA | RCC_APB2Periph_GPIOB |
	                        RCC_APB2Periph_GPIOC | RCC_APB2Periph_GPIOD |
	                        RCC_APB2Periph_GPIOE, ENABLE);

	GPIO_InitStructure.GPIO_Pin = GPIO_Pin_1;	         	 	//ENC28J60接收完成中断引脚
	GPIO_InitStructure.GPIO_Mode = GPIO_Mode_IPU;   	 		//内部上拉输入
	GPIO_Init(GPIOA, &GPIO_InitStructure);
}


void Delay(vu32 nCount)
{
	for(; nCount != 0; nCount--);
}

