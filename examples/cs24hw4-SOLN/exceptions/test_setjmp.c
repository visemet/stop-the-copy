#include <stdio.h>
#include "my_setjmp.h"
/*!
* This sentry-value is used on both ends of the jump-buffer in the tests, so
*/
#define SENTRY_VALUE 0xC8C8C8C8
/*!
* This helper function checks the jump results, to ensure that both setjmp()
* and longjmp() are called once, and that the expected and actual return-values
* are what they ought to be.
*/
void check_jmp_results(const char *type, int set, int lng, int expected_ret,
int actual_ret) {
if (set == 1 && lng == 1) {
if (expected_ret != 0 && actual_ret == expected_ret ||
expected_ret == 0 && actual_ret == 1) {
printf("PASS: %s setjmp/longjmp with return %d\n", type,
expected_ret);
}
else {
if (expected_ret == 0 && actual_ret != 1) {
printf("FAIL: %s setjmp/longjmp, longjmp(env, 0) didn't "
"return 1,\n\tactual return = %d\n", type, actual_ret);
}
else {
printf("FAIL: %s setjmp/longjmp with expected return %d,\n"
"\tactual return = %d\n", type, expected_ret, actual_ret);
}
}
}
else {
printf("FAIL: %s setjmp/longjmp didn't work (set = %d, lng = %d)\n",
type, set, lng);
}
}
/*!
* This function verifies that setjmp() and longjmp() can work within a single
* function call. No stack-frames are discarded.
*/
void test_intra_func(int expected_ret) {
volatile int actual_ret, set, lng;
int before = SENTRY_VALUE;
jmp_buf env;
int after = SENTRY_VALUE;
actual_ret = set = lng = 0;
if (actual_ret = setjmp(env)) {
lng++;
}
else {
/* setjmp() was called for the first time. longjmp() inside here. */
set++;
longjmp(env, expected_ret);
}
check_jmp_results("intra-function", set, lng, expected_ret, actual_ret);
if (before != SENTRY_VALUE || after != SENTRY_VALUE) {
printf("FAIL: sentry values were corrupted! before = 0x%x, "
"after = 0x%x\n", before, after);
}
}
/*!
* This is a simple function that recurses n times to set up n stack-frames, and
* then the function calls longjmp() to cut all of those frames off the stack.
* The function is about twice as long as it strictly has to be, since gcc is
* capable of performing tail-call optimization, and we want to make sure to
* detect when this is the situation so we can print a suitable warning.
*/
void recursive_longjmp(jmp_buf env, int n, int expected_ret,
void *parent_frame, int tailopt) {
void *my_frame = __builtin_frame_address(0);
/* gcc is able to apply some crazy optimizations, such as converting a
* recursive function into an iterative one if the recursive call is the
* last thing done in the current function. This is called "tail-call
* optimization." If this happens, it will thwart our test, so we make sure
* to identify when such a situation occurs.
*/
if (parent_frame != NULL && my_frame == parent_frame && !tailopt) {
printf("WARNING: gcc has applied tail-call optimization to "
"recursive_longjmp(),\n");
printf(" so this test is probably not going to be useful.\n");
tailopt = 1;
}
if (n > 0 && !tailopt) /* no point in recursing if we've been optimized */
recursive_longjmp(env, n - 1, expected_ret, my_frame, tailopt);
else
longjmp(env, expected_ret);
}
/*!
* This function verifies that setjmp() and longjmp() can work across multiple
* function calls. Multiple stack frames are discarded.
*/
void test_inter_func(int n, int expected_ret) {
volatile int actual_ret, set, lng;
int before = SENTRY_VALUE;
jmp_buf env;
int after = SENTRY_VALUE;
char desc[40];
set = lng = 0;
if (actual_ret = setjmp(env)) {
lng++;
}
else {
/* setjmp() was called for the first time. longjmp() inside here. */
set++;
recursive_longjmp(env, n, expected_ret,
/* parent_frame */ NULL, /* tailopt */ 0);
}
snprintf(desc, sizeof(desc), "inter-function (%d frames)", n);
check_jmp_results(desc, set, lng, expected_ret, actual_ret);
if (before != SENTRY_VALUE || after != SENTRY_VALUE) {
printf("FAIL: sentry values were corrupted! before = 0x%x, "
"after = 0x%x\n", before, after);
}
}
/*! This function performs various tests using the other testing functions. */
int main() {
printf("\n"
"==== Intra-Function setjmp()/longjmp(), nonzero longjmp() value ===="
"\n\n");
test_intra_func(43); /* longjmp(env, 43) */
printf("\n"
"==== Inter-Function setjmp()/longjmp(), nonzero longjmp() value ===="
"\n\n");
test_inter_func(3, 15); /* longjmp(env, 15) */
test_inter_func(10, 35); /* longjmp(env, 35) */
test_inter_func(30, 21); /* longjmp(env, 21) */
printf("\n"
"==== Intra-Function setjmp()/longjmp(), zero longjmp() value ===="
"\n\n");
test_intra_func(0); /* longjmp(env, 0) */
printf("\n"
"==== Inter-Function setjmp()/longjmp(), zero longjmp() value ===="
"\n\n");
test_inter_func(3, 0); /* longjmp(env, 0) */
test_inter_func(10, 0); /* longjmp(env, 0) */
test_inter_func(30, 0); /* longjmp(env, 0) */
printf("\n==== All done! ====\n\n");
return 0;
}