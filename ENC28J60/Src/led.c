#include "led.h"
void GpioConfigurationLed(void)
{
	GPIO_InitTypeDef  GPIO_InitStructure;

	/* Enable the GPIO_LED Clock */
	RCC_APB2PeriphClockCmd(RCC_APB2Periph_GPIOB
	                       | RCC_APB2Periph_GPIOC
	                       | RCC_APB2Periph_GPIOD, ENABLE);

	/* Configure the GPIO_LED pin */
	GPIO_InitStructure.GPIO_Mode = GPIO_Mode_Out_PP; 	//推挽输出
	GPIO_InitStructure.GPIO_Speed = GPIO_Speed_50MHz;

	GPIO_InitStructure.GPIO_Pin = GPIO_Pin_All ;
	GPIO_Init(GPIOC, &GPIO_InitStructure);


}

void DelayLED(unsigned int uiDly)
{
	while(uiDly--);
}

void led(void)
{
	GpioConfigurationLed();
	for(;;)
	{

		GPIOC ->ODR =   GPIO_Pin_13;	//熄灭(LED共阳极)

		DelayLED(0x54321);

		GPIOC ->ODR &= ~GPIO_Pin_13 ;	//点亮(LED共阳极)

		DelayLED(0x54321);
	}
}
