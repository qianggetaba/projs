

#include <windows.h>
#include <tlhelp32.h>
#include <direct.h>
#include <stdio.h>
#include <tlhelp32.h>
#include <tchar.h>

HANDLE hStdin;
DWORD fdwSaveOldMode;

BOOLEAN isQuit = false;

char pwd[MAX_PATH] = { 0 }; // 当前目录
char prog[MAX_PATH] = { 0 }; // prog to run in command system

HANDLE worker[4]; //保存新建进程的句柄
int    workerId = 0;

VOID ErrorExit(LPSTR lpszMessage);
VOID KeyEventProc(KEY_EVENT_RECORD ker);
VOID killall();
void printError(TCHAR* msg);
BOOL WINAPI CtrlHandler(DWORD fdwCtrlType);

void usage() {
	system("CLS");
	printf("[1]  stop  proxy\n");
	printf("[2]  start proxy\n");
	printf("[u]  show  usage\n");
	printf("enter command:");
}

int main(VOID)
{
	usage();
	SetConsoleCtrlHandler(CtrlHandler, TRUE);

	char* buffer;
	buffer = _getcwd(NULL, 0);
	strcat_s(pwd, MAX_PATH, buffer);
	strcat_s(pwd, MAX_PATH, "\\");

	DWORD cNumRead, fdwMode, i;
	INPUT_RECORD irInBuf[128];


	hStdin = GetStdHandle(STD_INPUT_HANDLE);
	GetConsoleMode(hStdin, &fdwSaveOldMode);

	fdwMode = ENABLE_WINDOW_INPUT | ENABLE_MOUSE_INPUT;
	SetConsoleMode(hStdin, fdwMode);

	while (!isQuit)
	{
		if (!ReadConsoleInput(
			hStdin,      // input buffer handle 
			irInBuf,     // buffer to read into 
			128,         // size of read buffer 
			&cNumRead)) // number of records read
		{
			char msg[] = "ReadConsoleInput";
			ErrorExit(msg);
		}

		for (i = 0; i < cNumRead; i++)
		{
			switch (irInBuf[i].EventType)
			{
			case KEY_EVENT: // keyboard input 
				KeyEventProc(irInBuf[i].Event.KeyEvent);
				break;
			default:
				char msg[] = "Unknown event typ";
				//ErrorExit(msg);
				break;
			}
		}
	}
	SetConsoleMode(hStdin, fdwSaveOldMode);
	return 0;
}

// 组装程序命令后，在system运行
VOID exeSimple(const char* exe) {
	ZeroMemory(prog, MAX_PATH);

	strcat_s(prog, MAX_PATH, pwd);
	strcat_s(prog, MAX_PATH, exe);
	system(prog);
}

// 在单独线程运行
DWORD WINAPI threadRun(void* exe) {
	char* progexe = (char*)exe;

	char progtmp[MAX_PATH] = { 0 };
	strcat_s(progtmp, MAX_PATH, pwd);
	strcat_s(progtmp, MAX_PATH, progexe);
	system(progtmp);

	return 0;
}

VOID exeSimpleThread(const char* exe) {
	strcpy_s(prog, MAX_PATH, exe);
	worker[workerId++] = CreateThread(NULL, 0, threadRun, prog, 0, NULL);
	Sleep(500);
}

VOID proxy() {
	exeSimple("sysproxy64.exe global http://127.0.0.1:1080");
}

VOID unproxy() {
	exeSimple("sysproxy64.exe off");
}

//退出处理
VOID quit() {
	unproxy();
	printf("quiting...\n");
	isQuit = true;
	killall();
}

VOID killall()
{
	HANDLE hProcessSnap;
	HANDLE hProcess;
	PROCESSENTRY32 pe32;
	DWORD dwPriorityClass;

	// Take a snapshot of all processes in the system.
	hProcessSnap = CreateToolhelp32Snapshot(TH32CS_SNAPPROCESS, 0);
	if (hProcessSnap == INVALID_HANDLE_VALUE)
	{
		TCHAR msg[] = TEXT("CreateToolhelp32Snapshot (of modules)");
		printError(msg);
		return;
	}

	// Set the size of the structure before using it.
	pe32.dwSize = sizeof(PROCESSENTRY32);

	// Retrieve information about the first process,
	// and exit if unsuccessful
	if (!Process32First(hProcessSnap, &pe32))
	{
		TCHAR msg[] = TEXT("Process32First");
		printError(msg); // show cause of failure
		CloseHandle(hProcessSnap);          // clean the snapshot object
	}

	// Now walk the snapshot of processes, and
	// display information about each process in turn
	do
	{
		//_tprintf(TEXT("\nPROCESS NAME:  %s"), pe32.szExeFile);

		// Retrieve the priority class.
		dwPriorityClass = 0;
		hProcess = OpenProcess(PROCESS_ALL_ACCESS, FALSE, pe32.th32ProcessID);
		if (hProcess == NULL) {
			TCHAR msg[] = TEXT("OpenProcess");
			//printError(msg);
		}
		else
		{
			dwPriorityClass = GetPriorityClass(hProcess);
			if (!dwPriorityClass) {
				TCHAR msg[] = TEXT("GetPriorityClass");
				//printError(msg);
			}
			CloseHandle(hProcess);
		}
		TCHAR v2ray[] = TEXT("v2ray.exe");
		TCHAR kcptun[] = TEXT("client_windows_amd64.exe");
		if (_wcsicmp(pe32.szExeFile, v2ray) == 0 || _wcsicmp(pe32.szExeFile, kcptun) == 0) {
			_tprintf(TEXT("v2ray.exe...........\n"));
			HANDLE handy;
			handy = OpenProcess(SYNCHRONIZE | PROCESS_TERMINATE, TRUE, pe32.th32ProcessID);
			TerminateProcess(handy, 0);
		}


	} while (Process32Next(hProcessSnap, &pe32));

	CloseHandle(hProcessSnap);
}

//处理所有按键
VOID KeyEventProc(KEY_EVENT_RECORD ker)
{
	// make sure key released
	if (ker.bKeyDown) return;

	printf("Key event: %d %c \n", ker.wVirtualKeyCode, ker.uChar.AsciiChar);

	char cmd = ker.uChar.AsciiChar;
	if (cmd == 'u') {
		usage();
		return;
	}

	if (cmd == 'q') {
		system("CLS");
		quit();
	}

	if (cmd == '1') {
		unproxy();
		printf("unproxy\n");
		return;
	}

	if (cmd == '2') {
		proxy();

		exeSimpleThread("v2ray.exe"); //使用config.json配置，要不然用 -config 路径太长

		exeSimpleThread("client_windows_amd64.exe -r \"176.122.157.253:13333\" -l \":23333\"");
		exeSimpleThread("client_windows_amd64.exe -r \"176.122.157.253:13334\" -l \":23334\"");
		exeSimpleThread("client_windows_amd64.exe -r \"176.122.157.253:13335\" -l \":23335\"");

		return;
	}
}

VOID ErrorExit(LPSTR lpszMessage)
{
	fprintf(stderr, "%s\n", lpszMessage);

	// Restore input mode on exit.
	SetConsoleMode(hStdin, fdwSaveOldMode);

	ExitProcess(0);
}

void printError(TCHAR* msg)
{
	DWORD eNum;
	TCHAR sysMsg[256];
	TCHAR* p;

	eNum = GetLastError();
	FormatMessage(FORMAT_MESSAGE_FROM_SYSTEM | FORMAT_MESSAGE_IGNORE_INSERTS,
		NULL, eNum,
		MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT), // Default language
		sysMsg, 256, NULL);

	// Trim the end of the line and terminate it with a null
	p = sysMsg;
	while ((*p > 31) || (*p == 9))
		++p;
	do { *p-- = 0; } while ((p >= sysMsg) &&
		((*p == '.') || (*p < 33)));

	// Display the message
	_tprintf(TEXT("\n  WARNING: %s failed with error %d (%s)"), msg, eNum, sysMsg);
}

BOOL WINAPI CtrlHandler(DWORD fdwCtrlType)
{
	switch (fdwCtrlType)
	{
		// Handle the CTRL-C signal. 
	case CTRL_C_EVENT:
		printf("Ctrl-C event\n\n");
		Beep(750, 300);
		return TRUE;

		// CTRL-CLOSE: confirm that the user wants to exit. 
	case CTRL_CLOSE_EVENT:
		quit();
		Beep(600, 200);
		printf("Ctrl-Close event\n\n");
		return TRUE;

		// Pass other signals to the next handler. 
	case CTRL_BREAK_EVENT:
		Beep(900, 200);
		printf("Ctrl-Break event\n\n");
		return FALSE;

	case CTRL_LOGOFF_EVENT:
		Beep(1000, 200);
		printf("Ctrl-Logoff event\n\n");
		return FALSE;

	case CTRL_SHUTDOWN_EVENT:
		Beep(750, 500);
		printf("Ctrl-Shutdown event\n\n");
		return FALSE;

	default:
		return FALSE;
	}
}