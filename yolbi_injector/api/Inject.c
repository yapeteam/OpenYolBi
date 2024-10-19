
// Copyright (c) 2012, Stephen Fewer of Harmony Security (www.harmonysecurity.com)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without modification, are permitted
// provided that the following conditions are met:
//
//     * Redistributions of source code must retain the above copyright notice, this list of
// conditions and the following disclaimer.
//
//     * Redistributions in binary form must reproduce the above copyright notice, this list of
// conditions and the following disclaimer in the documentation and/or other materials provided
// with the distribution.
//
//     * Neither the name of Harmony Security nor the names of its contributors may be used to
// endorse or promote products derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
// IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
// FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
// CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
// SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
// THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
// OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

#include <windows.h>
#include <stdio.h>
#include <wchar.h>
#include <locale.h>
#include "LoadLibraryR.h"

#pragma comment(lib, "Advapi32.lib")

#define RETURN_WITH_ERROR(e)                                  \
    {                                                         \
        printf("[-] %s. Error=%d\n", e, (int)GetLastError()); \
        return;                                               \
    }

// 将十六进制字符串转换为UTF-8字符串
char *hex_to_utf8(const char *hex)
{
    size_t len = strlen(hex);
    if (len % 2 != 0)
    {
        return NULL; // 十六进制字符串长度应为偶数
    }

    // 每个十六进制数转为一个字节，所以结果长度是输入长度的一半
    char *utf8_str = (char *)malloc(len / 2 + 1);
    if (!utf8_str)
        return NULL;

    for (size_t i = 0; i < len; i += 2)
    {
        char high = *hex++;
        char low = *hex++;
        sscanf(&high, "%1hhx", (unsigned char *)&utf8_str[i / 2]);
        sscanf(&low, "%1hhx", (unsigned char *)&utf8_str[i / 2] + 1);
    }
    utf8_str[len / 2] = '\0'; // 确保字符串以null结尾

    return utf8_str;
}

void Inject(int, const wchar_t *) __attribute__((used));

void Inject(int dwProcessId, const wchar_t *cpDllFile)
{
    setlocale(LC_ALL, "");
    wprintf(L"[+] Injecting DLL %s into process %d.\n", cpDllFile, dwProcessId);
    HANDLE hFile = NULL;
    LPVOID lpBuffer = NULL;
    DWORD dwBytesRead = 0;
    DWORD dwLength;
    hFile = CreateFileW(cpDllFile, GENERIC_READ, 0, NULL, OPEN_EXISTING, FILE_ATTRIBUTE_NORMAL, NULL);
    if (hFile == INVALID_HANDLE_VALUE)
        RETURN_WITH_ERROR("Failed to open the DLL file")
    dwLength = GetFileSize(hFile, NULL);
    if (dwLength == INVALID_FILE_SIZE || dwLength == 0)
        RETURN_WITH_ERROR("Failed to get the DLL file size")
    lpBuffer = HeapAlloc(GetProcessHeap(), 0, dwLength);
    if (!lpBuffer)
        RETURN_WITH_ERROR("Failed to get the DLL file size")
    if (ReadFile(hFile, lpBuffer, dwLength, &dwBytesRead, NULL) == FALSE)
    {
        RETURN_WITH_ERROR("Failed to alloc a buffer!")
    }
    HANDLE hModule = NULL;
    HANDLE hProcess = NULL;
    HANDLE hToken = NULL;
    TOKEN_PRIVILEGES priv = {0};

    if (OpenProcessToken(GetCurrentProcess(), TOKEN_ADJUST_PRIVILEGES | TOKEN_QUERY, &hToken))
    {
        priv.PrivilegeCount = 1;
        priv.Privileges[0].Attributes = SE_PRIVILEGE_ENABLED;

        if (LookupPrivilegeValue(NULL, SE_DEBUG_NAME, &priv.Privileges[0].Luid))
            AdjustTokenPrivileges(hToken, FALSE, &priv, 0, NULL, NULL);

        CloseHandle(hToken);
    }

    hProcess = OpenProcess(
        PROCESS_CREATE_THREAD | PROCESS_QUERY_INFORMATION | PROCESS_VM_OPERATION | PROCESS_VM_WRITE |
            PROCESS_VM_READ,
        FALSE, dwProcessId);
    if (!hProcess)
        RETURN_WITH_ERROR("Failed to open the target process")

    hModule = LoadRemoteLibraryR(hProcess, lpBuffer, dwLength, NULL);
    if (!hModule)
        RETURN_WITH_ERROR("Failed to inject the DLL")

    printf("[+] Injected the DLL into process %lu.\n", dwProcessId);

    WaitForSingleObject(hModule, -1);

    HeapFree(GetProcessHeap(), 0, lpBuffer);
    CloseHandle(hProcess);
}

#define MAX_TITLE_LENGTH 1024
#define MAX_PROCESS_NAME_LENGTH 20

// 获取进程 ID 与窗口标题的函数
int printProcessesByClass(const wchar_t *className)
{
    HWND hWnd = FindWindowW(className, NULL);
    int idx = 0;
    while (hWnd != NULL)
    {
        if (hWnd != NULL)
        {
            wchar_t title[MAX_TITLE_LENGTH];
            GetWindowTextW(hWnd, title, MAX_TITLE_LENGTH);

            DWORD pid;
            GetWindowThreadProcessId(hWnd, &pid);

            // 如果标题长度超过限制，则截断
            int len = wcslen(title);
            if (len > MAX_PROCESS_NAME_LENGTH)
            {
                wcsncpy_s(title + MAX_PROCESS_NAME_LENGTH - 3, 3, L"...", 3);
            }
            wprintf(L"[%d] Title: %ls pid: %lu\n", idx + 1, title, pid);
            idx++;
        }
        hWnd = FindWindowExW(NULL, hWnd, className, NULL);
    }
    return idx;
}

void printMinecraftProcesses() __attribute__((used));

void printMinecraftProcesses()
{
    setlocale(LC_ALL, "");
    int lwjglCount = printProcessesByClass(L"LWJGL");
    int glfwCount = printProcessesByClass(L"GLFW30");
    if (lwjglCount == 0 && glfwCount == 0)
    {
        wprintf(L"[-] No Minecraft instance found.\n");
    }
    else
    {
        wprintf(L"%d Minecraft instance found.\n", lwjglCount + glfwCount);
    }
}