#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <math.h>
#include <omp.h>

#define MAX_LINE_LENGTH 1024

typedef struct Node {
    int value;
    struct Node* next;
} Node;

void addNode(Node** head, int value) {
    Node* newNode = (Node*)malloc(sizeof(Node));
    newNode->value = value;
    newNode->next = NULL;

    if (*head == NULL) {
        *head = newNode;
    } else {
        Node* temp = *head;
        while (temp->next != NULL) {
            temp = temp->next;
        }
        temp->next = newNode;
    }
}

void printList(Node* head) {
    Node* temp = head;
    while (temp != NULL) {
        printf("%d ", temp->value);
        temp = temp->next;
    }
    printf("\n");
}

void saveListToFile(Node* head, const char* filename) {
    FILE* file = fopen(filename, "w");
    if (file == NULL) {
        perror("Erro ao abrir o arquivo");
        return;
    }

    Node* temp = head;
    while (temp != NULL) {
        fprintf(file, "%d\n", temp->value);
        temp = temp->next;
    }

    fclose(file);
}

int is_prime(int n){
  int p;
  int i, s;

  p = (n % 2 != 0 || n == 2);

  if (p)
  {
    s = sqrt(n);

    for (i = 3; p && i <= s; i += 2)
      if (n % i == 0)
        p = 0;
  }

  return p;
}

int main(int argc, char **argv) {
    double t1,t2;
    t1 = omp_get_wtime();
    char **lines = NULL; // Ponteiro para o vetor de linhas
    char tempLine[MAX_LINE_LENGTH]; // Buffer temporário para ler cada linha
    FILE *file;
    Node* head = NULL;
    int primes = 0, count = 0;
    omp_set_num_threads(atoi(argv[1]));
    // Abre o arquivo para leitura
    file = fopen("entrada.txt", "r");
    if (file == NULL) {
        perror("Erro ao abrir o arquivo");
        return 1;
    }

    // Lê cada linha do arquivo
    while (fgets(tempLine, MAX_LINE_LENGTH, file) != NULL) {
        // Realoca o vetor para adicionar mais uma linha
        char **tempPtr = realloc(lines, (count + 1) * sizeof(char *));
        if (tempPtr == NULL) {
            perror("Erro ao realocar memória");
            free(lines);
            fclose(file);
            return 1;
        }
        lines = tempPtr;

        // Remove a quebra de linha ao final, se houver
        tempLine[strcspn(tempLine, "\n")] = 0;

        // Aloca memória para a nova linha
        lines[count] = malloc(strlen(tempLine) + 1);
        if (lines[count] == NULL) {
            perror("Erro ao alocar memória para a linha");
            // Libera a memória alocada até agora
            for (int i = 0; i < count; i++) {
                free(lines[i]);
            }
            free(lines);
            fclose(file);
            return 1;
        }

        // Copia a linha lida para o vetor
        strcpy(lines[count], tempLine);
        count++;
    }

    fclose(file);

    // Imprime as linhas lidas para verificar
    #pragma omp parallel for reduction(+:primes)
    for (int i = 0; i < count; i++) {
            if (is_prime(atoi(lines[i]))){
                addNode(&head, atoi(lines[i]));
                primes++;
            }
        //printf("Linha %d: %d\n", i, atoi(lines[i]));
        free(lines[i]); // Libera a memória da linha
    }
    free(lines); // Libera o vetor de linhas

    //printList(head);
    saveListToFile(head, "lista.txt");
    // Liberando a memória alocada para a lista
    Node* temp;
    while (head != NULL) {
        temp = head;
        head = head->next;
        free(temp);
    }
    t2 = omp_get_wtime();
    printf("primos: %d\n", primes);
    printf("segundos: %f\n", t2-t1);
    return 0;
}
