/*	
====================================================================================
2011 | Jonathan Baker  | JazzGen  |  http://www.jonny-baker.com | http://github.com/JSBaker

All source code licenced under The MIT Licence
====================================================================================  
*/

	JazzGen {
	
	var bps,bpm;
	var keySigNote;
	var tempo;
	
	var chord7th;
	
	
	var origTempo;
	var scale;
	var pentMaj;

	var bass,piano,pianoKey,drums;
	var pKeyRelease = 0.2;
	var bassTot, pianoTot, drumTot;
	
	var freeFlag = false;
	var notelength = 0.25;

	var s, buffers, samplenames, filenames, repeattext;
	var ridePat, hatPat, kickPat,snarePat,splashPat;
	var sCount = 0;
	var tempMult;
	var tempoBoost = 0;
	var tempoChange = false;
	var tempoEnd = false;
	var diff,prev,best;
	var viChord = 5;
	var intervals;
	var splash=0.01;
	var solo = -1;
	var <>soloThresh = 1.3;
	var <>tempoThresh = 0.15;
	var gaussianThresh = 0.025;
	var soloNotes,soloWeights;
	var currentNote=0;
	var sPause = 0.01;
	var soloPause;
	var soloNote;
	var drumSoloWeights;
	var currentSolo;
	var splashWeights;
	var soloAcAmp=0.7;
	var soloistAmp = 1.15;
	var soloRepeat=false;
	var lastSolo;
	var sectionLength=8;
	var barLength = 8;
	var<>pkeyOct = 24;
	
	var bassOct = 24;
	var bRel,bsNote;
	var patIndex = 0;
	
	var root;
	var chordVar=false;
	var prevNote;
	
	var playChord;
	var chordInd,keyNote,psNote,psVar;
	var chordPat;
	var scores;
	var probs;
	var sectionScores;
	
	var bassTimes;
	var pianoTimes;
	var drumTimes;
	var drumSoloTimes;
	var bassNote,num;
	
	var bs,ps,ds,sc;
	var drumWait=0;
	var quickFill = false;
	var trip,sNote;
	
	var barCount=0;
	
	var tot;
	
	var noteVar;
	var noteVarWeights;
	
	var mainLoop;
	
	var changebpm,changenote;
	var jazzReset = false;
	
	var forcedSolo;
	var soloForce = false;
	
	var psoloNote, psoloNotes, psoloWeights;
	var pcurrentNote = 0;
	
	var manualLoad;
	
	*new { |s,buffers|
		^super.new.initJazzGen(s,buffers)
		
	}
	
	initJazzGen {|server, buffs|
	
	s = server ? Server.default;
	buffers = buffs ? manualLoad = true;
	
	if(manualLoad, { 
		repeattext = "/Users/jonathanbaker/Desktop/Super Collider Samples/JazzKitSamples/"; // paste the path to the samples folder here
		samplenames = ["jkick","jsnare","jhat","jride","jsplash","clappingCrowd"];
		filenames = samplenames.collect{|sample| repeattext++sample++".wav"};
		buffers = filenames.collect{|name| Buffer.read(s,name)};
	});
	
	s.latency = 0.05;
	
	
	chord7th = [0,2,4,6];
	
	noteVar = [-1,0,1];
	noteVarWeights = [[0.05,0.475,0.475],[0.475,0.05,0.475],[0.475,0.475,0.05]];
	
	lastSolo = solo;
	
	pentMaj = [0,1,2,4,5];
	
	soloNotes = [[0,1,2,4,5,4,2,1],[0,1,2,4,5,4,5,2],[0,1,2,4,5,8,9,10],[10,9,8,5,4,2,1,0],[10,9,8,5,8,9,5,2]];
	soloWeights = [[0.4,0.35,0.2,0.025,025],[0.45,0.1,0.4,0.025,0.025],[0.15,0.1,0.05,0.3,0.4],
				[0.45,0.4,0.1,0.025,0.025],[0.4,0.45,0.1,0.025,0.025,0]];
	
	psoloNotes = [[0,2,4,6,8,6,4,2],[0,2,4,6,8,6,8,4],[0,2,4,6,8,10,12,10],[12,10,8,6,4,2,4,0],[10,8,10,8,6,4,2,0]];
	
	drumSoloWeights = [[0.05,0.2,0.2,0.15,0.15,0.05,0.2],
					[0.1,0.2,0.2,0.15,0.1,0.05,0.2],
					[0.1,0.2,0,0.2,0.15,0.1,0.25],
					[0.1,0.1,0.05,0.3,0.2,0.2,0.05],
					[0.05,0.1,0.05,0.2,0.1,0.3,0.2],
					[0.05,0.05,0.05,0.2,0.3,0.25,0.1],
					[0.15,0.15,0.05,0.15,0.2,0.1,0.2]];					
	splashWeights = [	[0.7,0.3,0,0],[0,1,0,0],[0.85,0,0.15,0],[1,0,0,0],[1,0,0,0],[0,0,0,1],[0.3,0.1,0.6,0]];
	
	/////////////////Patterns
	ridePat = {arg pInd, nLeng,solo=false, am=1, amp=0.15;
	
	var rideTimes,rideAmps,rideSolos,rideSoloAmps;
	var pat,amps;
	//pInd.postln;
	rideTimes = [[nLeng],[nLeng],[(nLeng*0.667),(nLeng*0.333)]];
	rideAmps = [[0],[amp],[amp,(amp*0.5)]];
	
	rideSolos = [	[(nLeng*0.5),(nLeng*0.3333),(nLeng*0.1667)],[(nLeng*0.5),(nLeng*0.5)],
				[(nLeng*0.5),(nLeng*0.3333),(nLeng*0.1667)],[(nLeng*0.5),(nLeng*0.5)],
				[(nLeng*0.5),(nLeng*0.5)],[(nLeng*0.5),(nLeng*0.5)],[(nLeng*0.5),(nLeng*0.5)]];
	rideSoloAmps = [[amp,amp,(amp*0.5)],[amp,amp,(amp*0.5)],[amp,amp,(amp*0.5)],[amp,0],[0,0],[0,0],[amp,amp]];
	
	if(solo,	{pat = rideSolos[pInd];amps = rideSoloAmps[pInd]},
			{pat = rideTimes[pInd];amps = rideAmps[pInd]});
			
	Pbind(
		\dur, Pseq(pat,1),
		\amp,Pseq(amps,1),
		\ampMult, am,
		\bufnum,buffers[3],
		\instrument, \buff
		).play(tempo);
	};
	
	
	hatPat = {arg nLeng, am=1, amp = 0.2;
	amp = [0,amp].wchoose([0.05,0.95]);
	Pbind(
		\dur, Pseq([nLeng],1),
		\amp,amp,
		\ampMult, am,
		\bufnum,buffers[2],
		\instrument, \buff
		).play(tempo);
	};
	
	kickPat = {arg pInd, nLeng, solo=false, am=1,amp = 0.8;
	
	var kickTimes,kickAmps,kickSolos,kickSoloAmps;
	var pat,amps;
	
	kickTimes = [[nLeng],[nLeng],[nLeng]];
	kickAmps = [[0],[amp],[amp]];
	
	kickSolos = 	[[(nLeng*0.5),(nLeng*0.5)],[(nLeng*0.1666),(nLeng*0.1667),(nLeng*0.1667),(nLeng*0.5)],
				[(nLeng*0.5),(nLeng*0.5)],[(nLeng*0.5),(nLeng*0.5)],[(nLeng*0.5),(nLeng*0.5)],
				[(nLeng*0.333),(nLeng*0.333),(nLeng*0.334)],[(nLeng*0.5),(nLeng*0.5)]];
	kickSoloAmps = [[amp,0],[0,(amp*0.7),amp],[amp,amp],[amp,0],[0,0],[amp,amp,amp],[amp,amp]];
	
	if(solo,	{pat = kickSolos[pInd];amps = kickSoloAmps[pInd];},
			{pat = kickTimes[pInd];amps = kickAmps[pInd];});
			
	Pbind(
		\dur, Pseq(pat,1),
		\amp,Pseq(amps,1),
		\ampMult, am,
		\bufnum,buffers[0],
		\instrument, \buff
		).play(tempo);
	};
	
	
	
	snarePat = {arg pInd, nLeng, solo=false, am = 1,amp=0.8;
	
	var snareTimes, snareAmps,snareSolos,snareSoloAmps;
	var pat,amps;
	
	if((solo==false)&&(pInd==2),{
		
		sCount = sCount+1;
		if((sCount*0.275).coin, {pInd = 2;sCount=0},{pInd = 1});
	});
	
	//pInd.postln;	
	
	snareTimes = [[nLeng],[nLeng],[(nLeng*0.5),(nLeng*0.5)]];
	snareAmps = [[0],[amp],[0,(amp*0.7)]];
	
	snareSolos = [[(nLeng*0.5),(nLeng*0.5)],
				[(nLeng*0.5),(nLeng*0.3333),(nLeng*0.1667)],
				[(nLeng*0.3333),(nLeng*0.1667),(nLeng*0.3333),(nLeng*0.1667)],
				[(nLeng*0.5),(nLeng*0.1666),(nLeng*0.1667),(nLeng*0.1667)],
				[(nLeng*0.1666),(nLeng*0.1667),(nLeng*0.1667),(nLeng*0.1666),(nLeng*0.1667),(nLeng*0.1667)],
				[(nLeng*0.333),(nLeng*0.333),(nLeng*0.334)],
				[(nLeng*0.1666),(nLeng*0.1667),(nLeng*0.1667),(nLeng*0.1666),(nLeng*0.1667),(nLeng*0.1667)]];
	snareSoloAmps = [[0,amp],[0,amp,(amp*0.7)],[0,(amp*0.6),0,(amp*0.6)],[0,(amp*0.3),(amp*0.7),amp],
				[amp,(amp*0.3),(amp*0.7),amp,(amp*0.3),(amp*0.7)],[amp,amp,amp],
				[0,(amp*0.3),(amp*0.7),0,(amp*0.3),(amp*0.7)]];
	
	if(solo,	{pat = snareSolos[pInd];amps = snareSoloAmps[pInd];},
			{pat = snareTimes[pInd];amps = snareAmps[pInd];});
			
	Pbind(
		\dur, Pseq(pat,1),
		\amp,Pseq(amps,1),
		\ampMult, am,
		\bufnum,buffers[1],
		\instrument, \buff
		).play(tempo);
	};
	
	
	
	splashPat = {arg pInd,nLeng,rate=1, am = 1,amp = 0.7;
	var splashTimes,splashAmps;
	
	splashTimes = [[(nLeng*0.5),(nLeng*0.5)],[(nLeng*0.5),(nLeng*0.5)],[(nLeng*0.5),(nLeng*0.5)],[(nLeng*0.333),(nLeng*0.333),(nLeng*0.334)]];
	splashAmps = [[amp,0],[0,amp],[amp,amp],[amp,amp,amp]];
	
	
	
	Pbind(
		\dur, Pseq(splashTimes[pInd],1),
		\amp,Pseq(splashAmps[pInd],1),
		\ampMult, am,
		\bufnum,buffers[4],
		\rate, rate,
		\instrument, \buff
		).play(tempo);
	};
	
	
	this.loadSynths;	
	
	}
	
	jazz {|newbpm,note|
		
		newbpm = newbpm ? rrand(50,120);
		note = note ? rrand(48,59);
		this.setFeatures(newbpm,note);
		
		this.setMainLoop;
		//tempo.clear;
		mainLoop.play(tempo);//mainLoop;
		
		//tempo.schedAbs(tempo.beats.ceil, { mainLoop.play; 2 });
	
	}
	
	/*reJazz {|newbpm,note|
		
//		if(jazzReset==false,{
			changebpm = newbpm ? rrand(50,120);
			changenote = note ? rrand(48,59);
//			jazzReset = true;
//		},{
			this.stop;
			this.jazz(changebpm,changenote);
//			jazzReset=false;
//		});
	}*/
	
	stop {	
		tempo.clear;	
	}
	
	setFeatures {|newbpm, note|
	
		sectionScores = Array.fill(3,{0});
		drumSoloTimes = Array.newClear(4);
	
		bps = newbpm/120;//rrand(0.41,0.95);
		keySigNote = note;
		tempo = TempoClock(bps);
			
		"BPM: ".post; (bps*120).floor.postln;
		"Key: ".post;// keySigNote.postln;
		intervals = if(0.5.coin,{"Major".postln; [2,2,1,2,2,2,1]},{"Minor".postln; [2,1,2,2,1,2,2]});
		//if(0.5.coin,{intervals = [2,1,2,2,2,2,1];"jazz takeover!".postln;});
		
		origTempo = tempo;
		
		// create initial scale using generated root note and intervals
		prevNote = keySigNote-12;
		
		scale = Array.fill(48,{arg a; 
						if(a>0,{	
							prevNote = prevNote + intervals.wrapAt(a-1);
							prevNote;
						},{
							prevNote;
						})
						
					});
					
		scale.postln;
	}
	
	forceSolo {|fsolo|
	
		forcedSolo = fsolo ? 3.rand;
		
		
		soloForce = true;
	
	}
	
	
	
	}