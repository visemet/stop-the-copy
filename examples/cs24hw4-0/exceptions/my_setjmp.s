.globl my_setjmp
.globl my_longjmp
.text
    .align 4

my_setjmp:
    # Set up stack frame.
    push    %ebp
    movl    %esp, %ebp

    # Move the argument into a register
    movl    8(%ebp), %eax

    # Store the buffer into a register
    movl    %ebx, 0(%eax)
    movl    %esi, 4(%eax)
    movl    %edi, 12(%eax)

    # Store the address of the argument into a register (this is the index of
    # esp)
    leal    8(%ebp), %ecx   # use ecx as a temporary placeholder
    movl    %ecx, 16(%eax)

    # Store the return address into a register (this is the index of eip)
    movl    4(%ebp), %ecx   # once again, we're using ecx as a placeholder
    movl    %ecx, 20(%eax)

    # We now want to use the address of the caller frame (index of ebp) to
    # to replace the index of edi
    movl    (%ebp), %ecx    # placeholder ecx
    movl    %ecx, 12(%eax)

    # set_jmp should always return 0, so set the return register (always eax by
    # default) to 0
    xorl    %eax, %eax

    # Clean up stack frame.
    mov     %ebp, %esp
    pop     %ebp

    ret

my_longjmp:
    # No stack frame is needed in this function

    # Storing the jump buffer pointer and the return value
    movl    4(%esp), %ecx
    movl    8(%esp), %eax

    # Placeholder for the next step
    movl    $1, %ebx

    # my_setjmp initially returns 0, but, after my_longjmp is called, 0 can
    # never be returned again.  The below steps ensure this fact.
    testl   %eax, %eax  # checks whether or not eax is 0
    cmovzl  %ebx, %eax  # if it is, move 1 into eax

    # Move the register buffer values back to their proper locations
    movl    0(%ecx), %ebx
    movl    4(%ecx), %esi
    movl    8(%ecx), %edi
    movl    12(%ecx), %ebp
    movl    16(%ecx), %esp

    # This allows us to put the return address (the item stored at 20 in 
    # my_setjmp) where it needs to be, but without having to return after
    movl    20(%ecx), %edx
    jmp     *%edx
    