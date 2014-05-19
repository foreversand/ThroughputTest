//Copyright FAN XIAODONG 2014 
//The asm code refer to the ARM GCC Inline Assembler  programming tutorial


#include "app_sand_throughputtest_MemoryRWJni.h"
#include <time.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
//#include <android/log.h>
#define BYTE unsigned char
#define DWORD unsigned long
#define CHECK 10

//#define  D(x...) __android_log_print(ANDROID_LOG_INFO,"memoryrwjni",x)

struct timespec start;
struct timespec end;
/* return current time in microseconds */
static long during_us(void)
{
    return 1000000 * (end.tv_sec - start.tv_sec) + (end.tv_nsec - start.tv_nsec)/1000;
}
static double
now_ms(void)
{
    struct timespec res;
    clock_gettime(CLOCK_REALTIME, &res);
    return 1000.0*res.tv_sec + (double)res.tv_nsec/1e6;
}

static long DATA[16] = {256, 512, 1024, 2048, 4096, 8192, 16384, 32768, 65536, 131072, 262144, 524288, 1048576, 2097152, 4194304, 8388608};
static long TIMES[16] = {32768, 16384, 8192, 4096, 2048, 1024, 512, 256, 128, 64, 32, 16, 8, 4, 2, 1};



static void memread_32bytes(void *d, unsigned long n)
{
   
    asm volatile (
        ".Lvr32:                       \n\t"               
        "ldmia %0!, {r2-r9}  \n\t"        
        "subs %1, %1, #32   \n\t"
        "bne .Lvr32         \n\t"
        : 
        : "r" (d), "r" (n)
        : "r2", "r3","r4", "r5", "r6", "r7", "r8", "r9"
        );
}

static void memread_16bytes(void *d, unsigned long n)
{
   
    asm volatile (
        ".Lv2:                       \n\t"               
        "ldmia %0!, {r4-r7}  \n\t"        
        "subs %1, %1, #16   \n\t"
        "bne .Lv2         \n\t"
        : 
        : "r" (d), "r" (n)
        : "r4", "r5", "r6", "r7"
        );
}

static void memread_8bytes(void *d, unsigned long n)
{
    asm volatile (
        ".Lv3:                       \n\t"               
        "ldmia %0!, {r6-r7}  \n\t"        
        "subs %1, %1, #8   \n\t"
        "bne .Lv3         \n\t"
        :
        : "r" (d), "r" (n)
        : "r6", "r7"
        );
}

static void memread_4bytes(void *d, unsigned long n)
{
    asm volatile (
        ".Lv4:                       \n\t"               
        "ldr r4, [%0]  \n\t"        
        "subs %1, %1, #4   \n\t"
        "bne .Lv4         \n\t"
        :
        : "r" (d), "r" (n)
        : "r4"
        );
}

static void memread_2bytes(void *d, unsigned long n)
{
    asm volatile (
        ".Lv5:                       \n\t"               
        "ldrh r4, [%0]  \n\t"        
        "subs %1, %1, #2   \n\t"
        "bne .Lv5         \n\t"
        :
        : "r" (d), "r" (n)
        : "r4"
        );
}

static void memread_1byte(void *d, unsigned long n)
{
    asm volatile (
        ".Lv1:                       \n\t"               
        "ldrb r4, [%0] \n\t"        
        "subs %1, %1, #1   \n\t"
        "bne .Lv1         \n\t"
        :
        : "r" (d), "r" (n)
        : "r4"
        );
}

static void memwrite_32bytes(void *d, unsigned long n)
{
    asm volatile (
        ".Lvw32:                       \n\t"                     
        "stmia %0!, {r2-r9}  \n\t"
        "subs %1, %1, #32   \n\t"
        "bne .Lvw32         \n\t"
        :
        : "r" (d), "r" (n)
        : "r2", "r3","r4", "r5", "r6", "r7", "r8", "r9"
        );
}


static void memwrite_16bytes(void *d, unsigned long n)
{
    asm volatile (
        ".Lv8:                       \n\t"                     
        "stmia %0!, {r4-r7}  \n\t"
        "subs %1, %1, #16   \n\t"
        "bne .Lv8         \n\t"
        :
        : "r" (d), "r" (n)
        : "r4", "r5", "r6", "r7"
        );
}

static void memwrite_8bytes(void *d, unsigned long n)
{
    asm volatile (
        ".Lv9:                       \n\t"                     
        "stmia %0!, {r2-r3}  \n\t"
        "subs %1, %1, #8   \n\t"
        "bne .Lv9         \n\t"
        :
        : "r" (d), "r" (n)
        : "r2", "r3"
        );
}

static void memwrite_4bytes(void *d, unsigned long n)
{
    asm volatile (
        ".Lv10:                       \n\t"                     
        "str r4, [%0]  \n\t"
        "subs %1, %1, #4   \n\t"
        "bne .Lv10         \n\t"
        :
        : "r" (d), "r" (n)
        : "r4"
        );
}

static void memwrite_2bytes(void *d, unsigned long n)
{
    asm volatile (
        ".Lv11:                       \n\t"                     
        "strh r4, [%0]  \n\t"
        "subs %1, %1, #2   \n\t"
        "bne .Lv11         \n\t"
        :
        : "r" (d), "r" (n)
        : "r4"
        );
}

static void memwrite_1byte(void *d, unsigned long n)
{
    asm volatile (
        ".Step7:                       \n\t"                     
        "strb r4, [%0] \n\t"
        "subs %1, %1, #1   \n\t"
        "bne .Step7         \n\t"
        :
        : "r" (d), "r" (n)
        : "r4"
        );
}



static void memoryread(void *buf, unsigned long length, unsigned int mode)
{
    unsigned long residue;
    unsigned long block;
    unsigned long total = length;
    
    
    while(total > 0)
    {	
    	while(total  < (1 << mode)) {
    	    mode--;
    	    break;
    	}
    	residue = total % (1 << mode);
    	block = total - residue;
    	switch(mode) {
    	    case 0:
    	    	memread_1byte(buf, block);
    	    	break;
    	    case 1:
    	    	memread_2bytes(buf, block);
    	    	break;
    	    case 2:
    	    	memread_4bytes(buf, block);
    	    	break;
    	    case 3:
    	    	memread_8bytes(buf, block);
    	    	break;
            case 4:
    	    	memread_16bytes(buf, block);
    	    	break;
    	    case 5:
    	    	memread_32bytes(buf, block);
    	    	break;   
    	}
    	total = total - block;
    }
}

static void alignread(void * buf, unsigned long length)
{
    unsigned long patch;
    
    if((unsigned long)(buf) & 0xF) {
    	patch = 16 - ((unsigned long)buf & 0xF);
    	if(patch >= length) {
	    memoryread(buf, length, 0);
    	}
    	else {
    	    memoryread(buf, patch, 0);
    	    buf = (BYTE *)((unsigned long)buf + patch);
    	    memoryread(buf, length - patch, 5);
	}
    }
}

static void memorywrite(void *buf, unsigned long length, unsigned int mode)
{
    unsigned long residue;
    unsigned long block;
    unsigned long total = length;
    
    while(total > 0)
    {	
    	while(total  < (1 << mode)) {
    	    mode--;
    	    break;
    	}
    	residue = total % (1 << mode);
    	block = total - residue;
    	switch(mode) {
    	    case 0:
    	    	memwrite_1byte(buf, block);
    	    	break;
    	    case 1:
    	    	memwrite_2bytes(buf, block);
    	    	break;
    	    case 2:
    	    	memwrite_4bytes(buf, block);
    	    	break;
    	    case 3:
    	    	memwrite_8bytes(buf, block);
    	    	break;
            case 4:
    	    	memwrite_16bytes(buf, block);
    	    	break;
    	    case 5:
    	    	memwrite_32bytes(buf, block);
    	    	break;   
    	}
    	total = total - block;
    }
}

static void alignwrite(void * buf, unsigned long length)
{
    unsigned long patch;
    
    if((unsigned long)(buf) & 0xF) {
    	patch = 16 - ((unsigned long)buf & 0xF);
    	if(patch >= length) {
	    memorywrite(buf, length, 0);
    	}
    	else {
    	    memorywrite(buf, patch, 0);
    	    buf = (BYTE *)((unsigned long)buf + patch);
    	    memorywrite(buf, length - patch, 5);
	}
    }
}


JNIEXPORT jstring JNICALL Java_app_sand_throughputtest_MemoryRWJni_MemoryReadRate
  (JNIEnv * env, jobject arg, jlong datasize) {

    char*  str;
    char strbuffer[4096];
    long duringtime;   
    long rate;
    long length = datasize;
    //D("read %d bytes", length);
    asprintf(&str, "All the rate unit is MB/s.\nNormal Test : 32 bytes Read.\n");
    strlcpy(strbuffer, str, sizeof strbuffer);
    free(str);
    
    BYTE * buffer;
    buffer = (BYTE *) memalign(32, length);
    //for(long i = 0; i < length; i ++) {
    //	* (buffer + i) = i % 256;
    //}
    memset(buffer, 0xFF, length);
    for(int i = 0; i < CHECK; i++) {
    	clock_gettime(CLOCK_REALTIME, &start);
    	//D("read start %d s, %d ns", start.tv_sec, start.tv_nsec);
    	memoryread(buffer, length, 5);
        clock_gettime(CLOCK_REALTIME, &end);
        
        //D("read end %d s, %d ns", end.tv_sec, end.tv_nsec);
        duringtime = during_us();
        //D("read during %d us", duringtime);
     
        if(duringtime > 0)
        {
            rate = length/duringtime;
            //D("read %d bytes", length);
            //D("spend %d us", duringtime);
            //D("read rate %d MB/s", rate);
            asprintf(&str, "% 5d.                    % 5d\n", i + 1, rate);
            strlcat(strbuffer, str, sizeof strbuffer);
            free(str);
     
    
        }
        else{
        	asprintf(&str, "%5d.                    NaN\n", i +  1);
        	strlcat(strbuffer, str, sizeof strbuffer);
        	free(str);
        } 
    }
    
    asprintf(&str, "\nMode Test 1, 2, 4, 8, 16, 32 bytes Read\n");
    strlcat(strbuffer, str, sizeof strbuffer);
    free(str);
    
    for(int i = 0; i < 6; i++) {
    	clock_gettime(CLOCK_REALTIME, &start);
    	memoryread(buffer, length, i);
        clock_gettime(CLOCK_REALTIME, &end);
        duringtime = during_us();
     
        if(duringtime > 0)
        {
            rate = length/duringtime;
            //D("read %d bytes", length);
            //D("spend %d us", duringtime);
            //D("read rate %d MB/s", rate);
            asprintf(&str, "Mode% 5d.               % 5d\n", i, rate);
            strlcat(strbuffer, str, sizeof strbuffer);
            free(str);        
    
        }
        else{
        	asprintf(&str, "Mode% 5d.               NaN\n");
        	strlcpy(strbuffer, str, sizeof strbuffer);
        	free(str);
        } 
    }
    
    asprintf(&str, "\nDifferent size Buffer Read\n");
    strlcat(strbuffer, str, sizeof strbuffer);
    free(str);
    
    asprintf(&str, "                              1byte     2 bytes     4 bytes     8 bytes     16 bytes     32 bytes\n");
    strlcat(strbuffer, str, sizeof strbuffer);
    free(str);
    
    for(int j = 0; j < 16; j++) {
        asprintf(&str, "\n%8d bytes * %5d times     ", DATA[j], TIMES[j]);
        strlcat(strbuffer, str, sizeof strbuffer);
        free(str);
        BYTE * buffer2;
        buffer2 = (BYTE *) memalign(32, DATA[j]);
        memset(buffer2, 0xFF, DATA[j]);
        for(int i = 0; i < 6; i++) {
    	    clock_gettime(CLOCK_REALTIME, &start);
    	    for(int k = 0; k < TIMES[j]; k++) {
    	        memoryread(buffer2, DATA[j], i);
    	    }
            clock_gettime(CLOCK_REALTIME, &end);
            duringtime = during_us();
     
            if(duringtime > 0)
            {
                rate = DATA[15]/duringtime;
                asprintf(&str, "%5d          ", rate);
                strlcat(strbuffer, str, sizeof strbuffer);
                free(str);        
    
            }
            else{
            	asprintf(&str, "  NaN          ");
        	    strlcpy(strbuffer, str, sizeof strbuffer);
        	    free(str);
            } 
        }
        free(buffer2);
    }
    
    
    asprintf(&str, "\nDisalignment Test 16 bytes Read\n");
    strlcat(strbuffer, str, sizeof strbuffer);
    free(str);
    clock_gettime(CLOCK_REALTIME, &start);
    alignread(buffer + 1, length - 1);
    clock_gettime(CLOCK_REALTIME, &end);
    duringtime = during_us();
    //D("read %d us", duringtime);
    
    if(duringtime > 0)
    {
        rate = length/duringtime;
        //D("read %d bytes", length);
        //D("spend %d us", duringtime);
        //D("read rate %d MB/s", rate);
        asprintf(&str, "disalign:              %5d\n",  rate);
        strlcat(strbuffer, str, sizeof strbuffer);
        free(str);        
    
    }
    else{
    	asprintf(&str, "disalign:              NaN\n");
    	strlcpy(strbuffer, str, sizeof strbuffer);
    	free(str);
    }
         
         
         
    free(buffer);
    jstring rtn = env->NewStringUTF(strbuffer);
    return rtn;
}


JNIEXPORT jstring JNICALL Java_app_sand_throughputtest_MemoryRWJni_MemoryWriteRate
  (JNIEnv * env, jobject arg, jlong datasize) {
    long duringtime;   
    long rate;
    char*  str;
    char strbuffer[4096];
    long length = datasize;
    //D("write %d bytes", length);
    asprintf(&str, "\nNormal Test 16 bytes Write\n");
    strlcpy(strbuffer, str, sizeof strbuffer);
    free(str);
    
    BYTE * buffer;
    buffer = (BYTE *) memalign(32, length);
    memset(buffer, 0, length);
    for(int i = 0; i < CHECK; i++) {
        clock_gettime(CLOCK_REALTIME, &start);
        memorywrite(buffer, length, 5);
        clock_gettime(CLOCK_REALTIME, &end);

        
        duringtime = during_us();
        if(duringtime > 0)
        {
            rate = length/duringtime;
            //D("write %d bytes", length);
            //D("spend %d us", duringtime);
            //D("write rate %d MB/s", rate);
            asprintf(&str, "% 5d.                    % 5d\n", i + 1, rate);
            strlcat(strbuffer, str, sizeof strbuffer);
            free(str);

        }
        else{
        	asprintf(&str, "%5d.                    NaN\n", i +  1);
        	strlcat(strbuffer, str, sizeof strbuffer);
        	free(str);
        } 
    }
    
    asprintf(&str, "\nMode Test 1,2,4,8,16,32 bytes Write\n");
    strlcat(strbuffer, str, sizeof strbuffer);
    free(str);
    
    for(int i = 0; i < 6; i++) {
        clock_gettime(CLOCK_REALTIME, &start);
        memorywrite(buffer, length, i);
        clock_gettime(CLOCK_REALTIME, &end);
        
        //D("buffer[0-3] %d %d %d %d", buffer[0], buffer[1], buffer[2], buffer[3]);
        //D("buffer[4-7] %d %d %d %d", buffer[4], buffer[5], buffer[6], buffer[7]);
        //D("buffer[8-11] %d %d %d %d", buffer[8], buffer[9], buffer[10], buffer[11]);
        //D("buffer[12-15] %d %d %d %d", buffer[12], buffer[13], buffer[14], buffer[15]);
        //D("buffer[16-19] %d %d %d %d", buffer[16], buffer[17], buffer[19], buffer[20]);
        //D("buffer[20-23] %d %d %d %d", buffer[20], buffer[21], buffer[22], buffer[23]);
        //D("buffer[24-27] %d %d %d %d", buffer[24], buffer[25], buffer[26], buffer[27]);
        //D("buffer[28-31] %d %d %d %d", buffer[28], buffer[29], buffer[30], buffer[31]);
        
        
        duringtime = during_us();
        if(duringtime > 0)
        {
            rate = length/duringtime;
            //D("write %d bytes", length);
            //D("spend %d us", duringtime);
            //D("write rate %d MB/s", rate);
            asprintf(&str, "Mode% 5d.               % 5d\n", i, rate);
            strlcat(strbuffer, str, sizeof strbuffer);
            free(str);        
    
        }
        else{
        	asprintf(&str, "Mode% 5d.               NaN\n");
        	strlcpy(strbuffer, str, sizeof strbuffer);
        	free(str);
        } 
    }
    
    asprintf(&str, "\nDifferent size Buffer Write\n");
    strlcat(strbuffer, str, sizeof strbuffer);
    free(str);
    
    asprintf(&str, "                              1byte     2 bytes     4 bytes     8 bytes     16 bytes     32 bytes\n");
    strlcat(strbuffer, str, sizeof strbuffer);
    free(str);
    
    for(int j = 0; j < 16; j++) {
        asprintf(&str, "\n%8d bytes * %5d times     ", DATA[j], TIMES[j]);
        strlcat(strbuffer, str, sizeof strbuffer);
        free(str);
        BYTE * buffer2;
        buffer2 = (BYTE *) memalign(32, DATA[j]);
        //memset(buffer2, 0xFF, DATA[j]);
        for(int i = 0; i < 6; i++) {
    	    clock_gettime(CLOCK_REALTIME, &start);
    	    for(int k = 0; k < TIMES[j]; k++) {
    	        memorywrite(buffer2, DATA[j], i);
    	    }
            clock_gettime(CLOCK_REALTIME, &end);
            duringtime = during_us();
     
            if(duringtime > 0)
            {
                rate = DATA[15]/duringtime;
                asprintf(&str, "%5d          ", rate);
                strlcat(strbuffer, str, sizeof strbuffer);
                free(str);        
    
            }
            else{
            	asprintf(&str, "  NaN          ");
        	    strlcpy(strbuffer, str, sizeof strbuffer);
        	    free(str);
            } 
        }
        free(buffer2);
    }
    asprintf(&str, "\nDisalignment Test 16 bytes Write\n");
    strlcat(strbuffer, str, sizeof strbuffer);
    free(str);
   
    clock_gettime(CLOCK_REALTIME, &start);
    alignwrite(buffer + 1, length - 1);
    clock_gettime(CLOCK_REALTIME, &end);
    duringtime = during_us();
    
    if(duringtime > 0)
    {
        rate = length/duringtime;
        //D("read %d bytes", length);
        //D("spend %d us", duringtime);
        //D("read rate %d MB/s", rate);
         asprintf(&str, "disalign:               %5d\n",  rate);
        strlcat(strbuffer, str, sizeof strbuffer);
        free(str);        
    
    }
    else{
    	asprintf(&str, "disalign:               NaN\n");
    	strlcpy(strbuffer, str, sizeof strbuffer);
    	free(str);
    }
    
    free(buffer);
    jstring rtn = env->NewStringUTF(strbuffer);
    return rtn;
}
