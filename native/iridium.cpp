#include "iridium.h"
extern "C" {
#include "mbelib.h"

#include <stdlib.h>
#include <stdio.h>
#include <math.h>
#include <string.h>
}
#include <iostream>
using namespace std;

void Iridium::to_decode(const char *input, int in_size, char *output, int out_size){
    short int * result;
    result = (short int *)calloc(out_size,sizeof(short int));
    retrn(input, in_size, result);
    int count =0;
    for (int i = 0; i < out_size/2; i++){
        output[count++] = (char)result[i];
        output[count++] = (char)(result[i] >> 8);
    }
 }

Iridium::Iridium()
{
    
}

Iridium::~Iridium()
{
    
}
