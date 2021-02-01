package ru.konstantin_starikov.samsung.aliensquest;

import android.content.Context;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

public class Story {
    private static Situation introduction = new Situation(null);
    private static Situation prologue = new Situation(null);
    private static Situation aliensInvasion = new Situation(null);
    private static Situation dialogueWithAliens = new Situation(null);
    private static Situation warDialogue = new Situation(null);
    private static Situation dialogueEatingPeople = new Situation(null);
    private static Situation peopleDissatisfactionDialogue  = new Situation(null);
    private static Situation dialogueWithRebels= new Situation(null);
    private static Situation defeat  = new Situation(null);
    private static Situation win = new Situation(null);
    private static President president;

    public static void play(TextView text, Button nextReplicaButton, LinearLayout buttonsLayout, Context context) throws IOException {

/*        Scanner scanner = new Scanner(System.in);
        System.out.println("Напишите имя своего персонажа: ");
        president = new President(scanner.nextLine());*/
        president = new President("LOX");

        win = new Situation(
                new ReplicaWithoutAnswers("Вице-президент: Господин президент, " +
                        "у американского народа это получилось! Совсем недавно наши войска " +
                        "освободили Техас и Аризону, мы также ожидаем освобождения Монтаны, " +
                        "Небраски и Юты. Народ ликует!",
                        new ReplicaWithoutAnswers("*Вы одержали победу над инопланетянами-каннибалами*",
                                new EndReplica(win)
                        )
                )
        );

        defeat = new Situation(
                new ReplicaWithoutAnswers("*На следующий день были захвачены " +
                        "штаты Калифорния и Вермонт, ещё через день инопланетяне-каннибалы " +
                        "овладели восточными штатами и Вашингтон пал, а вместе с Вашингтоном пала " +
                        "и ваша власть, вас уже везут на родную планету инопланетян - Мурману*",
                        new EndReplica(defeat)
                )
        );

        warDialogue = new Situation(
                new ReplicaWithoutAnswers(
                        "3 дня спустя",
                        new ReplicaWithAnswers("Вице-президент: Наши войска терпят поражение в Монтане, " +
                                "Аризоне, Миннесоте, Небраске, но ещё удерживают Калифорнию, Орегон и все " +
                                "восточные штаты. Вряд-ли они смогут долго удерживать Калифорнию и Орегон " +
                                "дальше, а если эти штаты падут, то вскоре падёт и вся Америка! Что будем делать, " +
                                "господин Президент?",
                                new Answer("Используем супероружие",
                                        new ReplicaWithoutAnswers("Вице-президент: Раз ничего больше не остаётся...давайте будем использовать наше лазерное оружие…",
                                                new EndReplica(win)),
                                        () -> win.play(text, nextReplicaButton, buttonsLayout, context)),
                                new Answer("Заключим мир",
                                        new ReplicaWithoutAnswers("*Соединённые штаты Америки заключили мир с инопланетянами*", new EndReplica(dialogueEatingPeople)), () -> dialogueEatingPeople.play(text, nextReplicaButton, buttonsLayout, context)),
                                new Answer("Продолжим сражаться, мобилизуем все войска, что у нас есть.",
                                        new EndReplica(warDialogue),
                                        () ->
                                        {
                                            if(new Random().nextBoolean()) win.play(text, nextReplicaButton, buttonsLayout, context);
                                            else  defeat.play(text, nextReplicaButton, buttonsLayout, context);
                                        }
                                )
                        )
                )
        );

        dialogueWithRebels = new Situation(
                new ReplicaWithoutAnswers(
                        "25 сентября 2021 года",
                        new ReplicaWithoutAnswers(
                                "*Вице-президента убили, вы заперлись одни в своём кабинете*",
                                new ReplicaWithoutAnswers(
                                        "*Разъярённая толпа врывается в ваш кабинет*",
                                        new ReplicaWithAnswers(
                                                "Разъярённая толпа: Ты! Ты позволил им есть наших " +
                                                        "любимых домашних животных. Ты ужасный президент!!!",
                                                new Answer(
                                                        "А вы так хотите пойти воевать " +
                                                                "с инопланетянами?! Я ради вас " +
                                                                "стараюсь не идти с ними на " +
                                                                "конфликт!",
                                                        new ReplicaWithoutAnswers(
                                                                "Разъярённая толпа: Да, " +
                                                                        "мы не хотим, чтобы " +
                                                                        "наших кошек кто-то ел, " +
                                                                        "так что сейчас мы тебя " +
                                                                        "убъём, захватим власть и " +
                                                                        "объявим пришельцам войну!",
                                                                new ReplicaWithoutAnswers("*Вы были " +
                                                                        "убиты разъярённой толпой*",
                                                                        new EndReplica(warDialogue)))),
                                                new Answer("Если вы сейчас не уйдёте, " +
                                                        "я нажму на красную кнопку и, в " +
                                                        "скором времени, здесь окажется " +
                                                        "несколько сотен военных, вооружённых " +
                                                        "пистолетами, автоматами и гранатами",
                                                        new ReplicaWithoutAnswers(
                                                                "Разъярённая толпа: А мы их не " +
                                                                        "боимся, тем более, мы сейчас " +
                                                                        "тебя убъём, захватим власть и " +
                                                                        "объявим пришельцам войну!",
                                                                new ReplicaWithoutAnswers(
                                                                        "*Вы были убиты разъярённой толпой*",
                                                                        new EndReplica(warDialogue)
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                )
        );

        peopleDissatisfactionDialogue = new Situation(
                new ReplicaWithoutAnswers(
                        "14 сентября 2021 года",
                        new ReplicaWithAnswers(
                                "Вице-президент: В мою машину люди кидают камни! Население недовольно тем, " +
                                        "что мы позволяем инопланетянам есть наших котов. Народ бунтует!",
                                new Answer(
                                        "Может, действительно пришло время объявить войну инопланетянам-каннибалам, сообщите Пентагону, чтобы подготовили американские войска к войне",
                                        new ReplicaWithoutAnswers("Вице-президент: Будет сделано!", new EndReplica(dialogueWithRebels)),
                                        () -> warDialogue.play(text, nextReplicaButton, buttonsLayout, context)
                                ),
                                new Answer("Пусть бунтуют! Я, " + president.name + ", - мудрый президент, " +
                                        "который лучше пожертвует жизнями тысяч животных, чем миллионов людей!",
                                        new ReplicaWithoutAnswers("Вице-президент: Меня так " +
                                                "когда-нибудь могут убить, но пусть лучше умру я, чем миллионы американцев!",
                                                new EndReplica(dialogueWithRebels)),
                                        () -> dialogueWithRebels.play(text, nextReplicaButton, buttonsLayout, context)
                                )
                        )
                )
        );

        dialogueEatingPeople = new Situation(
                new ReplicaWithoutAnswers(
                        "Неделю спустя",
                        new ReplicaWithAnswers(
                                "Вице-президент: Мистер-президент, у меня к вам довольно грустные новости, " +
                                        "среди людей ходят слухи, что инопланетяне оказались каннибалами! Они " +
                                        "едят тех котов, которых у нас забирают, наши кошки у них считаются " +
                                        "деликатесом. И наша разведка эти слухи недавно подтвердила.",
                                new Answer("Это ужасно!!! Объявляем им войну. Во славу котиков!",
                                        new EndReplica(peopleDissatisfactionDialogue),
                                        () ->  warDialogue.play(text, nextReplicaButton, buttonsLayout, context)),
                                new Answer("Пусть едят, зато мы с ними в мире и люди живы.",
                                        new EndReplica(peopleDissatisfactionDialogue),
                                        () -> dialogueEatingPeople.play(text, nextReplicaButton, buttonsLayout, context))
                        ))
        );

        dialogueWithAliens = new Situation(
                new ReplicaWithoutAnswers(
                        "В этот же день",
                        new ReplicaWithoutAnswers(
                                "Вице-президент: Они идут, садитесь за стол переговоров, господин Президент.",
                                new ReplicaWithoutAnswers(
                                        "Пришелец1: Мяу-мяу-мя-мяу-мяу-мя-мя-мя-мяу-мя-мя-мяу-мяу-мяу-мяу-мя-мяу.",
                                        new ReplicaWithoutAnswers("Переводчик с кошачьего: “Перейдём сразу к " +
                                                "делу, мы прилетели на Землю, потому что вы, земляне, удерживаете " +
                                                "в плену наших собратьев, котов, и нам абсолютно не нравится, как вы " +
                                                "с ними обращаетесь.",
                                                new ReplicaWithoutAnswers("Пришелец2: Мя-мяу! Мяу-мяу-мяу! " +
                                                        "Мя-мя-мяу-мяу! Мя-мя-мя-мяу-мяу!",
                                                        new ReplicaWithoutAnswers("Переводчик с кошачьего:" +
                                                                " “Они живут на помойках! Едят консервированный " +
                                                                "мусор! А главное, многих из них вы лишили " +
                                                                "возможности завести котят! Земляне, вы - звери!”",
                                                                new ReplicaWithoutAnswers("Пришелец1: Мя-мяу. " +
                                                                        "Мяу-мяу-мя-мяу. Мя-мя-мя-мяу-мя-мя-мяу-мяу.",
                                                                        new ReplicaWithAnswers("Переводчик с кошачьего: " +
                                                                                "“Мы предлагаем вам 2 варианта. " +
                                                                                "Первый - вы будете отдавать нам по " +
                                                                                "1000 своих кошек в день и мы не будем " +
                                                                                "с вами воевать. Второй - вы не отдаёте" +
                                                                                " нам своих кошек, мы на вас нападаем " +
                                                                                "и силой их забираем.”",
                                                                                new Answer("Платить " +
                                                                                        "дань в 1000 кошек в день",
                                                                                        new ReplicaWithoutAnswers(
                                                                                                "Пришелец1: Мя-мя-мяу!",
                                                                                                new ReplicaWithoutAnswers(
                                                                                                        "Переводчик с кошачьего: “Вы сделали разумный выбор!”",
                                                                                                        new EndReplica (dialogueEatingPeople))), () -> peopleDissatisfactionDialogue.play(text, nextReplicaButton, buttonsLayout, context)),
                                                                                new Answer("Объявить инопланетянам войну",
                                                                                        new ReplicaWithoutAnswers(
                                                                                                "Пришелец2: Мя-мяу-мяу!",
                                                                                                new ReplicaWithoutAnswers(
                                                                                                        "Переводчик с кошачьего: “Тогда мы вас уничтожим!”",
                                                                                                        new EndReplica (dialogueEatingPeople))), () -> warDialogue.play(text, nextReplicaButton, buttonsLayout, context))
                                                                        )
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                )
        );

        aliensInvasion = new Situation(
                new ReplicaWithoutAnswers(
                        "10 июня того же года",
                        new ReplicaWithAnswers(
                                "Вице-президент: Господин " + president.name + ". Недалеко от Белого дома " +
                                        "приземлился какой-то вертолёт шаровидной формы и…. Оттуда вышли " +
                                        "странные существа с головами котов и телами змей. Народ в панике! " +
                                        "Я тоже в панике!!! Они уже направляются сюда, в Белый дом, " +
                                        "а мы даже не знаем их языка.",
                                new Answer("Сейчас скачаем приложение “Переводчик с кошачьего” и сможем друг " +
                                        "друга понимать",
                                        new ReplicaWithoutAnswers("Вице-президент: Попрошу весь персонал " +
                                                "установить это приложение",
                                                new EndReplica(dialogueWithAliens))),
                                new Answer("Ну так ищите человека, который знает кошачий!!!",
                                        new ReplicaWithoutAnswers("Вице-президент: Пентагон уже ищет",
                                                new EndReplica(dialogueWithAliens)))
                        )
                )
        );

        prologue = new Situation(
                new ReplicaWithAnswers(
                        "Вице-президент: Здравствуйте, мистер " + president.name + ". Наши " +
                                "вооружённые силы заметили неопознанные летающие объекты вблизи Земли, как думаете, " +
                                "что это может быть?",
                        new Answer("Очевидно, опять российский ракеты",
                                new ReplicaWithAnswers("Вице-президент: Тогда пока мобилизуем войска?",
                                        new Answer("Мобилизуйте", new ReplicaWithoutAnswers("Хорошо, будет сделано", new EndReplica(aliensInvasion))),
                                        new Answer("Нет, зачем тратить бюджет страны попусту?", new ReplicaWithoutAnswers("Да, господин Президент", new EndReplica(aliensInvasion))))
                        ),
                        new Answer("Я думаю, это инопланетное вторжение...",
                                new ReplicaWithAnswers("Вице-президент: Тогда пока мобилизуем войска?",
                                        new Answer("Мобилизуйте", new ReplicaWithoutAnswers("Хорошо, будет сделано", new EndReplica(aliensInvasion))),
                                        new Answer("Нет, зачем тратить бюджет страны попусту?", new ReplicaWithoutAnswers("Да, господин Президент", new EndReplica(aliensInvasion))))
                        ),
                        new Answer("Что вы там опять курите? Какие неопознанные летающие объекты?!", new ReplicaWithoutAnswers("Извините, что побеспокоил вас, мистер Президент, больше этого не повторится", new EndReplica(aliensInvasion))
                        )
                ));

        introduction = new Situation(
                new ReplicaWithoutAnswers(
                        "*В этой небольшой игре вы окажитесь президентом " +
                                "Соединённых штатов Америки и познаете, как непросто им быть. \n" +
                                "Для того, чтобы переходить на следующую реплику, нажимайте клавишу Enter*",
                        new EndReplica(prologue)
                )
        );

        introduction.play(text, nextReplicaButton, buttonsLayout, context);
    }
}
