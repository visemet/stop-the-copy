.globl my_setjmp
.text
my_setjmp:
pushl %ebp
movl %esp, %ebp
# Use %ecx to access the jump-buffer, since it's caller-save.
movl 8(%ebp), %ecx
movl %esp, (%ecx) # Stack pointer
movl 4(%esp), %edx # Caller's return address, from stack!
movl %edx, 4(%ecx)
movl (%esp), %edx # Caller's value for %ebp, from stack!
movl %edx, 8(%ecx)
movl %ebx, 12(%ecx) # Callee-save registers
movl %esi, 16(%ecx)
movl %edi, 20(%ecx)
xorl %eax, %eax # setjmp() returns 0; so do we.
popl %ebp
ret

.globl my_longjmp
my_longjmp:
pushl %ebp
movl %esp, %ebp
# longjmp()'s second argument is a value for setjmp to return. Pull
# this value off the stack into %eax. If the value is 0, longjmp()
# is specified to cause setjmp() to return 1, to avoid infinite loops.
movl 12(%ebp), %eax
# Conditionally set %eax to 1, if 0 was passed to my_longjmp().
movl $1, %ecx
test %eax, %eax
cmovz %ecx, %eax
# longjmp()'s first argument is the environment to jump back to.
# Retrieve this so that we can restore our registers.
movl 8(%ebp), %ecx
movl (%ecx), %esp # Chop down the stack!
movl 4(%ecx), %edx # Restore return-address
movl %edx, 4(%esp)
movl 8(%ecx), %edx # Restore caller's %ebp so when we return,
movl %edx, (%esp) # caller can access their local vars.
movl 12(%ecx), %ebx # Callee-save registers
movl 16(%ecx), %esi
movl 20(%ecx), %edi
# When we return, it will appear to the caller that setjmp() has
# returned a second time, since the stack has been recombobulated
# to return to exactly that location.
popl %ebp
ret