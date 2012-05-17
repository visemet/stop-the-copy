.globl my_setjmp 
.globl my_longjmp
.text
    .align 4

my_setjmp:
# set up the stack frame
pushl   %ebp
movl    %esp, %ebp

/* Memory address for storing registers:
 *  (%ebp)      is the previous frame pointer
 *  (%ebp + 4)  is the return instruction address
 *  (%ebp + 8)  is the argument
 */
movl    8(%ebp), %eax

movl    %ebx, 0(%eax)   #jmp_buf[index of ebx]
movl    %esi, 4(%eax)   #jmp_buf[index of esi]
movl    %edi, 12(%eax)   #jmp_buf[index of edi]

# stores the address of argument on stack in jmp_buf[index of esp]
leal    8(%ebp), %ecx
movl    %ecx, 16(%eax)

# stores return address in jmp_buf[index of eip]
movl    4(%ebp), %ecx
movl    %ecx, 20(%eax)

# stores the address of caller frame pointer in jmp_buf[index of ebp]
movl    (%ebp), %ecx
movl    %ecx, 12(%eax)

# clears %eax to return 0
xorl    %eax, %eax

# restore stack and frame pointers and return
movl    %ebp, %esp
popl    %ebp
ret

my_longjmp:
# We don't have to save stack frames

movl    4(%esp), %ecx   # jmp_buf pointer
movl    8(%esp), %eax   # return value

movl    $1, %ebx

testl   %eax, %eax
cmovzl  %ebx, %eax      

# restore register values
movl    0(%ecx), %ebx
movl    4(%ecx), %esi
movl    8(%ecx), %edi
movl    12(%ecx), %ebp
movl    16(%ecx), %esp

movl    20(%ecx), %edx

jmp     *%edx